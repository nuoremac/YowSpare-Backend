package yowyob.comops.stock.api.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.StockMovement;
import yowyob.comops.stock.api.domain.port.in.StockMovementUseCase;
import yowyob.comops.stock.api.domain.port.out.StockLevelRepositoryPort;
import yowyob.comops.stock.api.domain.port.out.StockMovementRepositoryPort;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockMovementService implements StockMovementUseCase {
    private final StockMovementRepositoryPort movementRepository;
    private final StockLevelRepositoryPort stockLevelRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Flux<StockMovement> getAllMovements(UUID organizationId) {
        return securityUtils.getUserContext().flatMapMany(ctx -> {
            if (ctx.getAgencyId() == null) {
                return movementRepository.findByOrganizationId(organizationId);
            } else {
                return movementRepository.findByOrganizationIdAndAgencyId(organizationId, ctx.getAgencyId());
            }
        });
    }

    @Override
    public Mono<StockMovement> getMovementById(UUID id) {
        return movementRepository.findById(id)
                .flatMap(movement -> securityUtils.getUserContext().flatMap(ctx -> {
                    if (ctx.getAgencyId() != null) {
                        boolean isRelated = ctx.getAgencyId().equals(movement.getSourceAgencyId()) ||
                                            ctx.getAgencyId().equals(movement.getDestinationAgencyId());
                        if (!isRelated) return Mono.empty();
                    }
                    return Mono.just(movement);
                }));
    }

    @Override
    @Transactional
    public Mono<StockMovement> createDraft(StockMovement movement) {
        return securityUtils.getUserContext().flatMap(ctx -> {
            // Validation Scope Ecriture
            if (ctx.getAgencyId() != null) {
                boolean isSourceMe = ctx.getAgencyId().equals(movement.getSourceAgencyId());
                boolean isDestMe = ctx.getAgencyId().equals(movement.getDestinationAgencyId());
                if (!isSourceMe && !isDestMe) {
                    return Mono.error(new IllegalStateException("You can only manage movements involving your agency"));
                }
            }

            movement.setStatus(StockMovement.MovementStatus.DRAFT);
            if (movement.getDate() == null) movement.setDate(Instant.now());
            if (movement.getReference() == null) movement.setReference("DRAFT-" + UUID.randomUUID().toString().substring(0,8));

            return movementRepository.save(movement);
        });
    }

    @Override
    @Transactional
    public Mono<StockMovement> validateMovement(UUID movementId, UUID validatorId) {
        return movementRepository.findById(movementId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Movement not found")))
                .flatMap(movement -> securityUtils.getUserContext().flatMap(ctx -> {
                    
                    // Validation Scope Validation
                    if (ctx.getAgencyId() != null) {
                        boolean isRelated = ctx.getAgencyId().equals(movement.getSourceAgencyId()) ||
                                            ctx.getAgencyId().equals(movement.getDestinationAgencyId());
                        if (!isRelated) {
                            return Mono.error(new IllegalStateException("Access Denied: Cannot validate movement outside your agency scope."));
                        }
                    }

                    if (movement.getStatus() == StockMovement.MovementStatus.VALIDATED) {
                        return Mono.error(new IllegalStateException("Movement already validated"));
                    }

                    return applyStockImpact(movement)
                            .then(Mono.defer(() -> {
                                movement.setStatus(StockMovement.MovementStatus.VALIDATED);
                                movement.setValidatedBy(validatorId);
                                movement.setValidatedAt(Instant.now());
                                return movementRepository.save(movement);
                            }));
                }));
    }

    private Mono<Void> applyStockImpact(StockMovement movement) {
        return Flux.fromIterable(movement.getItems())
                .flatMap(item -> {
                    Mono<Void> sourceOp = Mono.empty();
                    Mono<Void> destOp = Mono.empty();

                    if (movement.getSourceAgencyId() != null) {
                        sourceOp = stockLevelRepository.adjustStock(
                                item.getProductId(), 
                                movement.getSourceAgencyId(), 
                                -item.getQuantity(), 
                                Instant.now()
                        );
                    }

                    if (movement.getDestinationAgencyId() != null) {
                        destOp = stockLevelRepository.adjustStock(
                                item.getProductId(), 
                                movement.getDestinationAgencyId(), 
                                item.getQuantity(), 
                                Instant.now()
                        );
                    }

                    return sourceOp.then(destOp);
                })
                .then();
    }
}