package yowyob.comops.spareapi.analytics.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.analytics.dto.AnalyticsRecommendationDto;
import yowyob.comops.spareapi.analytics.entity.AnalyticsRecommendationEntity;
import yowyob.comops.spareapi.analytics.repository.AnalyticsRecommendationRepository;
import yowyob.comops.spareapi.integrations.stock.StockApiClient;
import yowyob.comops.spareapi.integrations.stock.StockLevelResponse;
import yowyob.comops.spareapi.integrations.stock.StockMovementItemResponse;
import yowyob.comops.spareapi.integrations.stock.StockMovementResponse;
import yowyob.comops.spareapi.policy.repository.LocationPolicyRepository;
import yowyob.comops.spareapi.reservation.repository.ReservationRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AnalyticsService {
    private final StockApiClient stockApiClient;
    private final ReservationRepository reservationRepository;
    private final LocationPolicyRepository locationPolicyRepository;
    private final AnalyticsRecommendationRepository repository;

    public AnalyticsService(StockApiClient stockApiClient,
                            ReservationRepository reservationRepository,
                            LocationPolicyRepository locationPolicyRepository,
                            AnalyticsRecommendationRepository repository) {
        this.stockApiClient = stockApiClient;
        this.reservationRepository = reservationRepository;
        this.locationPolicyRepository = locationPolicyRepository;
        this.repository = repository;
    }

    public Flux<AnalyticsRecommendationDto> list(UUID tenantId, UUID agencyId) {
        return repository.list(tenantId, agencyId).map(AnalyticsService::toDto);
    }

    public Flux<AnalyticsRecommendationDto> recompute(UUID tenantId,
                                                      UUID agencyId,
                                                      String authorizationHeader,
                                                      String tenantHeader,
                                                      int days) {
        int windowDays = Math.max(1, Math.min(days, 365));
        Instant cutoff = Instant.now().minus(windowDays, ChronoUnit.DAYS);

        Mono<List<StockLevelResponse>> levelsMono = stockApiClient.getStockLevels(authorizationHeader, tenantHeader);
        Mono<List<StockMovementResponse>> movementsMono = stockApiClient.getMovements(authorizationHeader, tenantHeader);

        Instant expiryNow = Instant.now();
        Mono<Map<UUID, Integer>> reservedByProduct = reservationRepository.findActiveForAgency(tenantId, agencyId)
                .filter(r -> r.getExpiresAt() == null || r.getExpiresAt().isAfter(expiryNow))
                .collectMultimap(r -> r.getProductId())
                .map(mm -> {
                    Map<UUID, Integer> out = new HashMap<>();
                    mm.forEach((productId, rows) -> {
                        if (productId == null) return;
                        int sum = 0;
                        for (var r : rows) {
                            Integer q = r.getQuantity();
                            if (q != null && q > 0) sum += q;
                        }
                        out.put(productId, sum);
                    });
                    return out;
                });

        Mono<Map<UUID, Integer>> ropByProduct = locationPolicyRepository.maxReorderPointByProduct(tenantId, agencyId)
                .collectMap(LocationPolicyRepository.MaxRopAgg::getProductId,
                        a -> a.getReorderPoint() == null ? 0 : a.getReorderPoint());

        return Mono.zip(levelsMono, movementsMono, reservedByProduct, ropByProduct)
                .flatMapMany(tuple -> {
                    List<StockLevelResponse> levels = tuple.getT1();
                    List<StockMovementResponse> movements = tuple.getT2();
                    Map<UUID, Integer> reservedMap = tuple.getT3();
                    Map<UUID, Integer> ropMap = tuple.getT4();

                    Map<UUID, Integer> onHandByProduct = new HashMap<>();
                    for (StockLevelResponse level : levels) {
                        if (level.productId() == null) continue;
                        if (level.agencyId() != null && !level.agencyId().equals(agencyId)) continue;
                        onHandByProduct.put(level.productId(), level.quantity() == null ? 0 : level.quantity());
                    }

                    Map<UUID, Integer> usageTotals = usageTotalsForAgency(movements, agencyId, cutoff);
                    Map<UUID, String> abcByProduct = abcClassify(usageTotals);

                    List<AnalyticsRecommendationEntity> computed = new ArrayList<>();
                    Instant now = Instant.now();

                    for (Map.Entry<UUID, Integer> e : onHandByProduct.entrySet()) {
                        UUID productId = e.getKey();
                        int onHand = e.getValue() == null ? 0 : e.getValue();
                        int reserved = reservedMap.getOrDefault(productId, 0);
                        int available = Math.max(0, onHand - reserved);

                        int used = usageTotals.getOrDefault(productId, 0);
                        BigDecimal avgDailyUsage = BigDecimal.valueOf(used)
                                .divide(BigDecimal.valueOf(windowDays), 4, RoundingMode.HALF_UP);

                        BigDecimal daysOfCover = null;
                        if (avgDailyUsage.compareTo(BigDecimal.ZERO) > 0) {
                            daysOfCover = BigDecimal.valueOf(available)
                                    .divide(avgDailyUsage, 4, RoundingMode.HALF_UP);
                        }

                        int rop = ropMap.getOrDefault(productId, 0);
                        Integer suggested = rop > 0 ? Math.max(0, rop - available) : 0;

                        BigDecimal risk = stockoutRisk(daysOfCover);

                        computed.add(AnalyticsRecommendationEntity.builder()
                                .id(UUID.randomUUID())
                                .tenantId(tenantId)
                                .agencyId(agencyId)
                                .productId(productId)
                                .avgDailyUsage(avgDailyUsage)
                                .daysOfCover(daysOfCover)
                                .abcClass(abcByProduct.getOrDefault(productId, "C"))
                                .stockoutRisk(risk)
                                .suggestedReorderQty(suggested)
                                .computedAt(now)
                                .createdAt(now)
                                .updatedAt(now)
                                .build());
                    }

                    return Flux.fromIterable(computed)
                            .flatMap(rec -> upsert(tenantId, agencyId, rec))
                            .map(AnalyticsService::toDto);
                });
    }

    private Mono<AnalyticsRecommendationEntity> upsert(UUID tenantId, UUID agencyId, AnalyticsRecommendationEntity rec) {
        Instant now = Instant.now();
        return repository.findOne(tenantId, agencyId, rec.getProductId())
                .defaultIfEmpty(new AnalyticsRecommendationEntity())
                .flatMap(cur -> {
                    boolean isNew = cur.getId() == null;
                    AnalyticsRecommendationEntity next = AnalyticsRecommendationEntity.builder()
                            .id(cur.getId() != null ? cur.getId() : rec.getId())
                            .newEntity(isNew)
                            .tenantId(tenantId)
                            .agencyId(agencyId)
                            .productId(rec.getProductId())
                            .avgDailyUsage(rec.getAvgDailyUsage())
                            .daysOfCover(rec.getDaysOfCover())
                            .abcClass(rec.getAbcClass())
                            .stockoutRisk(rec.getStockoutRisk())
                            .suggestedReorderQty(rec.getSuggestedReorderQty())
                            .computedAt(rec.getComputedAt())
                            .createdAt(cur.getCreatedAt() != null ? cur.getCreatedAt() : now)
                            .updatedAt(now)
                            .build();
                    return repository.save(next);
                });
    }

    private Map<UUID, Integer> usageTotalsForAgency(List<StockMovementResponse> movements, UUID agencyId, Instant cutoff) {
        Map<UUID, Integer> totals = new HashMap<>();
        for (StockMovementResponse m : movements) {
            if (m == null) continue;
            if (!"OUT".equalsIgnoreCase(m.type())) continue;
            if (!"VALIDATED".equalsIgnoreCase(m.status())) continue;
            Instant ts = m.validatedAt() != null ? m.validatedAt() : m.date();
            if (ts != null && ts.isBefore(cutoff)) continue;
            if (m.sourceAgencyId() != null && !m.sourceAgencyId().equals(agencyId)) continue;
            if (m.items() == null) continue;
            for (StockMovementItemResponse item : m.items()) {
                if (item == null || item.productId() == null) continue;
                int qty = item.quantity() == null ? 0 : item.quantity();
                totals.merge(item.productId(), qty, Integer::sum);
            }
        }
        return totals;
    }

    private Map<UUID, String> abcClassify(Map<UUID, Integer> totals) {
        if (totals.isEmpty()) return Map.of();
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        double grandTotal = sorted.stream().mapToDouble(e -> e.getValue() == null ? 0 : e.getValue()).sum();
        if (grandTotal <= 0) return Map.of();

        Map<UUID, String> out = new HashMap<>();
        double cumulative = 0;
        for (Map.Entry<UUID, Integer> e : sorted) {
            cumulative += e.getValue() == null ? 0 : e.getValue();
            double share = cumulative / grandTotal;
            String cls = share <= 0.80 ? "A" : (share <= 0.95 ? "B" : "C");
            out.put(e.getKey(), cls);
        }
        return out;
    }

    private BigDecimal stockoutRisk(BigDecimal daysOfCover) {
        if (daysOfCover == null) return BigDecimal.valueOf(0.20).setScale(4, RoundingMode.HALF_UP);
        if (daysOfCover.compareTo(BigDecimal.valueOf(7)) < 0) return BigDecimal.valueOf(0.90).setScale(4, RoundingMode.HALF_UP);
        if (daysOfCover.compareTo(BigDecimal.valueOf(14)) < 0) return BigDecimal.valueOf(0.60).setScale(4, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(0.20).setScale(4, RoundingMode.HALF_UP);
    }

    private static AnalyticsRecommendationDto toDto(AnalyticsRecommendationEntity e) {
        return new AnalyticsRecommendationDto(
                e.getProductId(),
                e.getAvgDailyUsage(),
                e.getDaysOfCover(),
                e.getAbcClass(),
                e.getStockoutRisk(),
                e.getSuggestedReorderQty(),
                e.getComputedAt()
        );
    }
}
