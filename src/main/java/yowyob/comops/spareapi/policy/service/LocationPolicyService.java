package yowyob.comops.spareapi.policy.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.policy.dto.LocationPolicyDto;
import yowyob.comops.spareapi.policy.dto.UpsertLocationPolicyRequest;
import yowyob.comops.spareapi.policy.entity.LocationPolicyEntity;
import yowyob.comops.spareapi.policy.repository.LocationPolicyRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class LocationPolicyService {
    private final LocationPolicyRepository repository;

    public LocationPolicyService(LocationPolicyRepository repository) {
        this.repository = repository;
    }

    public Flux<LocationPolicyDto> listForBin(UUID tenantId, UUID agencyId, String binCode) {
        return repository.findAllForBin(tenantId, agencyId, normalizeBin(binCode))
                .map(LocationPolicyService::toDto);
    }

    public Flux<LocationPolicyDto> listForBinAndProduct(UUID tenantId, UUID agencyId, String binCode, UUID productId) {
        String norm = normalizeBin(binCode);
        Mono<LocationPolicyDto> generic = repository.findGeneric(tenantId, agencyId, norm).map(LocationPolicyService::toDto);
        Mono<LocationPolicyDto> specific = repository.findForProduct(tenantId, agencyId, norm, productId).map(LocationPolicyService::toDto);
        return Flux.concat(generic, specific).onErrorResume(e -> Flux.empty());
    }

    public Flux<LocationPolicyDto> listForAgency(UUID tenantId, UUID agencyId, UUID productId) {
        Flux<LocationPolicyEntity> src = productId == null
                ? repository.findAllForAgency(tenantId, agencyId)
                : repository.findAllForAgencyAndProduct(tenantId, agencyId, productId);
        return src.map(LocationPolicyService::toDto);
    }

    public Mono<LocationPolicyDto> upsert(UUID tenantId,
                                         UUID agencyId,
                                         String binCode,
                                         UUID productId,
                                         UpsertLocationPolicyRequest req) {
        Instant now = Instant.now();
        String norm = normalizeBin(binCode);

        Mono<LocationPolicyEntity> existing = (productId == null)
                ? repository.findGeneric(tenantId, agencyId, norm)
                : repository.findForProduct(tenantId, agencyId, norm, productId);

        return existing.defaultIfEmpty(new LocationPolicyEntity())
                .flatMap(cur -> {
                    boolean isNew = cur.getId() == null;
                    LocationPolicyEntity next = LocationPolicyEntity.builder()
                            .id(cur.getId() != null ? cur.getId() : UUID.randomUUID())
                            .newEntity(isNew)
                            .tenantId(tenantId)
                            .agencyId(agencyId)
                            .binCode(norm)
                            .productId(productId)
                            .minQty(req.minQty())
                            .maxQty(req.maxQty())
                            .safetyStock(req.safetyStock())
                            .reorderPoint(req.reorderPoint())
                            .cycleCountDays(req.cycleCountDays())
                            .createdAt(cur.getCreatedAt() != null ? cur.getCreatedAt() : now)
                            .updatedAt(now)
                            .build();
                    return repository.save(next);
                })
                .map(LocationPolicyService::toDto);
    }

    public Mono<Void> delete(UUID tenantId, UUID agencyId, String binCode, UUID productId) {
        String norm = normalizeBin(binCode);
        return (productId == null)
                ? repository.deleteGeneric(tenantId, agencyId, norm)
                : repository.deleteForProduct(tenantId, agencyId, norm, productId);
    }

    private static LocationPolicyDto toDto(LocationPolicyEntity e) {
        return new LocationPolicyDto(
                e.getId(),
                e.getAgencyId(),
                e.getBinCode(),
                e.getProductId(),
                e.getMinQty(),
                e.getMaxQty(),
                e.getSafetyStock(),
                e.getReorderPoint(),
                e.getCycleCountDays(),
                e.getUpdatedAt()
        );
    }

    private String normalizeBin(String bin) {
        return bin == null ? "" : bin.trim();
    }
}
