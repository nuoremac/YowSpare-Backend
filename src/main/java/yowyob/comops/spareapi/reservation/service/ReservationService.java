package yowyob.comops.spareapi.reservation.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.integrations.stock.StockApiClient;
import yowyob.comops.spareapi.integrations.stock.StockLevelResponse;
import yowyob.comops.spareapi.reservation.dto.AvailabilityRowDto;
import yowyob.comops.spareapi.reservation.dto.CreateReservationRequest;
import yowyob.comops.spareapi.reservation.dto.ReservationDto;
import yowyob.comops.spareapi.reservation.entity.ReservationEntity;
import yowyob.comops.spareapi.reservation.repository.ReservationRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReservationService {
    private final ReservationRepository repository;
    private final StockApiClient stockApiClient;

    public ReservationService(ReservationRepository repository, StockApiClient stockApiClient) {
        this.repository = repository;
        this.stockApiClient = stockApiClient;
    }

    public Mono<ReservationDto> create(UUID tenantId, String createdBy, CreateReservationRequest req) {
        Instant now = Instant.now();
        ReservationEntity entity = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .newEntity(true)
                .tenantId(tenantId)
                .agencyId(req.agencyId())
                .productId(req.productId())
                .quantity(req.quantity())
                .status("ACTIVE")
                .referenceType(req.referenceType())
                .referenceId(req.referenceId())
                .note(req.note())
                .createdBy(createdBy)
                .expiresAt(req.expiresAt())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return repository.save(entity).map(ReservationService::toDto);
    }

    public Flux<ReservationDto> search(UUID tenantId, UUID agencyId, UUID productId, String status) {
        return repository.search(tenantId, agencyId, productId, status).map(ReservationService::toDto);
    }

    public Mono<ReservationDto> updateStatus(UUID tenantId, UUID id, String status) {
        String next = status == null ? "" : status.trim().toUpperCase();
        return repository.findById(id)
                .filter(e -> tenantId.equals(e.getTenantId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Reservation not found.")))
                .flatMap(e -> {
                    e.setStatus(next);
                    e.setUpdatedAt(Instant.now());
                    return repository.save(e);
                })
                .map(ReservationService::toDto);
    }

    public Mono<List<AvailabilityRowDto>> availability(UUID tenantId,
                                                       UUID agencyId,
                                                       String authorizationHeader,
                                                       String tenantHeader) {
        if (agencyId == null) {
            return Mono.error(new IllegalArgumentException("agencyId is required."));
        }

        // Avoid relying on projection mapping for aggregate queries; sum in Java from entity mapping.
        // Reservations are typically low volume per tenant+agency, so this stays fast and predictable.
        Instant now = Instant.now();
        Mono<Map<UUID, Integer>> reservedByProduct = repository.findActiveForAgency(tenantId, agencyId)
                .filter(r -> r.getExpiresAt() == null || r.getExpiresAt().isAfter(now))
                .collectMultimap(ReservationEntity::getProductId)
                .map(mm -> {
                    Map<UUID, Integer> out = new HashMap<>();
                    mm.forEach((productId, rows) -> {
                        if (productId == null) return;
                        int sum = 0;
                        for (ReservationEntity r : rows) {
                            Integer q = r.getQuantity();
                            if (q != null && q > 0) sum += q;
                        }
                        out.put(productId, sum);
                    });
                    return out;
                });

        Mono<List<StockLevelResponse>> levels = stockApiClient.getStockLevels(authorizationHeader, tenantHeader);

        return Mono.zip(levels, reservedByProduct)
                .map(tuple -> toAvailability(agencyId, tuple.getT1(), tuple.getT2()));
    }

    private List<AvailabilityRowDto> toAvailability(UUID agencyId,
                                                    List<StockLevelResponse> levels,
                                                    Map<UUID, Integer> reservedByProduct) {
        Map<UUID, AvailabilityRowDto> out = new HashMap<>();
        for (StockLevelResponse level : levels) {
            if (level.productId() == null) continue;
            if (level.agencyId() != null && !level.agencyId().equals(agencyId)) continue;
            int onHand = level.quantity() == null ? 0 : level.quantity();
            int reserved = reservedByProduct.getOrDefault(level.productId(), 0);
            int available = Math.max(0, onHand - reserved);
            out.put(level.productId(), new AvailabilityRowDto(agencyId, level.productId(), onHand, reserved, available));
        }
        return out.values().stream().toList();
    }

    private static ReservationDto toDto(ReservationEntity e) {
        return new ReservationDto(
                e.getId(),
                e.getAgencyId(),
                e.getProductId(),
                e.getQuantity(),
                e.getStatus(),
                e.getReferenceType(),
                e.getReferenceId(),
                e.getNote(),
                e.getCreatedBy(),
                e.getExpiresAt(),
                e.getUpdatedAt()
        );
    }
}
