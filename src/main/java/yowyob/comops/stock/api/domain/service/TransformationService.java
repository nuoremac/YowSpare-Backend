package yowyob.comops.stock.api.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.ProductTransformation;
import yowyob.comops.stock.api.domain.model.StockMovement;
import yowyob.comops.stock.api.domain.model.StockMovementItem;
import yowyob.comops.stock.api.domain.model.ProductTransformation.TransformationStatus;
import yowyob.comops.stock.api.domain.model.TransformationItem.TransformationItemType;
import yowyob.comops.stock.api.domain.port.in.ProductTransformationUseCase;
import yowyob.comops.stock.api.domain.port.in.StockMovementUseCase;
import yowyob.comops.stock.api.domain.port.out.ProductTransformationRepositoryPort;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransformationService implements ProductTransformationUseCase {
    private final ProductTransformationRepositoryPort transformationRepository;
    private final StockMovementUseCase movementUseCase;
    private final SecurityUtils securityUtils;

    @Override
    public Flux<ProductTransformation> getAll(UUID organizationId) {
        return securityUtils.getUserContext().flatMapMany(ctx -> {
            if (ctx.getAgencyId() == null) {
                return transformationRepository.findAllByOrganizationId(organizationId);
            }
            return transformationRepository.findAllByOrganizationIdAndAgencyId(organizationId, ctx.getAgencyId());
        });
    }

    @Override
    public Mono<ProductTransformation> getById(UUID id) {
        return transformationRepository.findById(id);
    }

    @Override
    @Transactional
    public Mono<ProductTransformation> create(ProductTransformation pt) {
        return securityUtils.getUserContext().flatMap(ctx -> {
            // Validation du scope
            if (ctx.getAgencyId() != null && !ctx.getAgencyId().equals(pt.getAgencyId())) {
                return Mono.error(new IllegalStateException("Cannot create transformation for another agency"));
            }

            pt.setStatus(TransformationStatus.DRAFT);
            if (pt.getDate() == null)
                pt.setDate(Instant.now());
            if (pt.getReference() == null)
                pt.setReference("MFG-" + System.currentTimeMillis());

            return transformationRepository.save(pt);
        });
    }

    @Override
    @Transactional
    public Mono<ProductTransformation> validate(UUID id, UUID userId) {
        return transformationRepository.findById(id)
                .flatMap(pt -> {
                    if (pt.getStatus() != TransformationStatus.DRAFT) {
                        return Mono.error(new IllegalStateException("Already validated or cancelled"));
                    }

                    // 1. Mouvement de SORTIE (Consommation des Inputs)
                    List<StockMovementItem> outItems = pt.getInputs().stream()
                            .map(i -> StockMovementItem.builder()
                                    .productId(i.getProductId())
                                    .quantity(i.getQuantity())
                                    .build())
                            .collect(Collectors.toList());

                    Mono<StockMovement> outMovement = Mono.empty();
                    if (!outItems.isEmpty()) {
                        outMovement = movementUseCase.createDraft(StockMovement.builder()
                                .organizationId(pt.getOrganizationId())
                                .sourceAgencyId(pt.getAgencyId()) // On sort de l'agence
                                .type(StockMovement.MovementType.OUT)
                                .reference("MFG-OUT-" + pt.getReference())
                                .notes("Consumption for " + pt.getReference())
                                .createdBy(userId)
                                .items(outItems)
                                .build())
                                .flatMap(m -> movementUseCase.validateMovement(m.getId(), userId));
                    }

                    // 2. Mouvement d'ENTRÉE (Production des Outputs)
                    List<StockMovementItem> inItems = pt.getOutputs().stream()
                            .map(i -> StockMovementItem.builder()
                                    .productId(i.getProductId())
                                    .quantity(i.getQuantity())
                                    .build())
                            .collect(Collectors.toList());

                    Mono<StockMovement> inMovement = Mono.empty();
                    if (!inItems.isEmpty()) {
                        inMovement = movementUseCase.createDraft(StockMovement.builder()
                                .organizationId(pt.getOrganizationId())
                                .destinationAgencyId(pt.getAgencyId()) // On rentre dans l'agence
                                .type(StockMovement.MovementType.IN)
                                .reference("MFG-IN-" + pt.getReference())
                                .notes("Production from " + pt.getReference())
                                .createdBy(userId)
                                .items(inItems)
                                .build())
                                .flatMap(m -> movementUseCase.validateMovement(m.getId(), userId));
                    }

                    // 3. Validation finale
                    return outMovement.then(inMovement).then(Mono.defer(() -> {
                        pt.setStatus(TransformationStatus.VALIDATED);
                        pt.setUpdatedAt(Instant.now());
                        return transformationRepository.save(pt);
                    }));
                });
    }
}