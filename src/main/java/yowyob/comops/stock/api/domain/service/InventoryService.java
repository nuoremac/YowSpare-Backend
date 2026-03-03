package yowyob.comops.stock.api.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.*;
import yowyob.comops.stock.api.domain.port.in.InventoryUseCase;
import yowyob.comops.stock.api.domain.port.in.StockMovementUseCase;
import yowyob.comops.stock.api.domain.port.out.InventoryRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockLevelEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcProductRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcStockLevelRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryUseCase {
    private final InventoryRepositoryPort inventoryRepository;
    private final R2dbcStockLevelRepository stockLevelRepository;
    private final R2dbcProductRepository productRepository;
    private final StockMovementUseCase movementUseCase;

    @Override
    @Transactional
    public Mono<InventorySession> initiateSession(InventorySession session) {
        session.setStatus(InventorySession.InventoryStatus.OPEN);
        session.setStartDate(Instant.now());
        if (session.getReference() == null)
            session.setReference("INV-" + System.currentTimeMillis());

        return inventoryRepository.save(session)
                .flatMap(savedSession -> {
                    Flux<InventoryCount> countsFlux;

                    if (savedSession.getCategoryIdScope() != null) {

                        countsFlux = getSnapshotForCategory(savedSession.getOrganizationId(),
                                savedSession.getAgencyId(), savedSession.getCategoryIdScope());
                    } else {

                        countsFlux = getSnapshotForAgency(savedSession.getOrganizationId(), savedSession.getAgencyId());
                    }

                    return countsFlux.collectList()
                            .flatMap(counts -> {
                                savedSession.setCounts(counts);

                                return inventoryRepository.save(savedSession);
                            });
                });
    }

    /**
     * Crée le snapshot pour tous les produits de l'agence.
     */
    private Flux<InventoryCount> getSnapshotForAgency(UUID orgId, UUID agencyId) {

        return stockLevelRepository.findByOrganizationIdAndAgencyId(orgId, agencyId)
                .map(this::mapStockToCount);
    }

    /**
     * Crée le snapshot filtré par catégorie.
     */
    private Flux<InventoryCount> getSnapshotForCategory(UUID orgId, UUID agencyId, UUID categoryId) {

        return productRepository.findByOrganizationId(orgId)
                .filter(p -> categoryId.equals(p.getCategoryId()))
                .flatMap(product -> stockLevelRepository.findByProductIdAndAgencyId(product.getId(), agencyId)
                        .defaultIfEmpty(
                                new StockLevelEntity(null, orgId, product.getId(), agencyId, 0, 0, Instant.now()))
                        .map(this::mapStockToCount));
    }

    private InventoryCount mapStockToCount(StockLevelEntity stock) {
        return InventoryCount.builder()
                .productId(stock.getProductId())
                .theoreticalQuantity(stock.getQuantity())
                .physicalQuantity(null)
                .variance(null)
                .build();
    }

    @Override
    public Mono<Void> submitCounts(UUID sessionId, List<InventoryCount> counts) {
        return inventoryRepository.findById(sessionId)
                .flatMap(session -> {
                    if (session.getStatus() != InventorySession.InventoryStatus.OPEN) {
                        return Mono.error(new IllegalStateException("Session not open"));
                    }

                    List<InventoryCount> updatedCounts = session.getCounts().stream()
                            .map(existingLine -> {

                                InventoryCount submission = counts.stream()
                                        .filter(c -> c.getProductId().equals(existingLine.getProductId()))
                                        .findFirst()
                                        .orElse(null);

                                if (submission != null) {
                                    existingLine.setPhysicalQuantity(submission.getPhysicalQuantity());

                                    int theory = existingLine.getTheoreticalQuantity() != null
                                            ? existingLine.getTheoreticalQuantity()
                                            : 0;
                                    existingLine.setVariance(submission.getPhysicalQuantity() - theory);
                                }
                                return existingLine;
                            }).collect(Collectors.toList());

                    session.setCounts(updatedCounts);
                    return inventoryRepository.save(session);
                }).then();
    }

    @Override
    public Mono<InventorySession> getSessionById(UUID id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public Flux<InventorySession> getSessionsByAgency(UUID agencyId) {
        return inventoryRepository.findByAgencyId(agencyId);
    }

    @Override
    @Transactional
    public Mono<InventorySession> validateSession(UUID sessionId, UUID validatorId) {
        return inventoryRepository.findById(sessionId)
                .flatMap(session -> {
                    if (session.getStatus() == InventorySession.InventoryStatus.VALIDATED) {
                        return Mono.error(new IllegalStateException("Already validated"));
                    }

                    List<StockMovementItem> adjustmentItems = session.getCounts().stream()
                            .filter(c -> c.getVariance() != null && c.getVariance() != 0)
                            .map(c -> StockMovementItem.builder()
                                    .productId(c.getProductId())
                                    .quantity(Math.abs(c.getVariance()))

                                    .quantity(c.getVariance())
                                    .build())
                            .collect(Collectors.toList());

                    Mono<Void> adjustmentProcess = Mono.empty();

                    if (!adjustmentItems.isEmpty()) {

                        StockMovement adjustment = StockMovement.builder()
                                .organizationId(session.getOrganizationId())
                                .type(StockMovement.MovementType.ADJUSTMENT)
                                .sourceAgencyId(session.getAgencyId())
                                .date(Instant.now())
                                .reference("ADJ-" + session.getReference())
                                .notes("Inventory Adjustment")
                                .createdBy(validatorId)
                                .items(adjustmentItems)
                                .build();

                        adjustmentProcess = movementUseCase.createDraft(adjustment)
                                .flatMap(draft -> movementUseCase.validateMovement(draft.getId(), validatorId))
                                .then();
                    }

                    return adjustmentProcess.then(Mono.defer(() -> {
                        session.setStatus(InventorySession.InventoryStatus.VALIDATED);
                        session.setValidatedBy(validatorId);
                        session.setValidatedDate(Instant.now());
                        return inventoryRepository.save(session);
                    }));
                });
    }
}