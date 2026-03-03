package yowyob.comops.stock.api.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.StockMovement;
import yowyob.comops.stock.api.domain.model.StockMovementItem;
import yowyob.comops.stock.api.domain.port.in.StockMovementUseCase;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
@Tag(name = "Stock Movements", description = "Gestion des flux logistiques")
public class StockMovementController {
    
    private final StockMovementUseCase movementService; 
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Historique des mouvements")
    @PreAuthorize("hasAuthority('MOVEMENT:READ')")
    public Flux<StockMovement> getAllMovements() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(movementService::getAllMovements);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MOVEMENT:READ')")
    public Mono<ResponseEntity<StockMovement>> getMovementById(@PathVariable UUID id) {
        return movementService.getMovementById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un mouvement (Brouillon)")
    @ApiResponse(responseCode = "201", description = "Mouvement créé")
    @PreAuthorize("hasAuthority('MOVEMENT:CREATE')")
    public Mono<ResponseEntity<StockMovement>> createDraft(@RequestBody CreateMovementRequest request) {
        return securityUtils.getCurrentOrganizationId()
                .flatMap(orgId -> securityUtils.getCurrentUserId().flatMap(userId -> {
                    StockMovement movement = StockMovement.builder()
                            .organizationId(orgId)
                            .type(StockMovement.MovementType.valueOf(request.getType()))
                            .sourceAgencyId(request.getSourceAgencyId())
                            .destinationAgencyId(request.getDestinationAgencyId())
                            .thirdPartyId(request.getThirdPartyId())
                            .notes(request.getNotes())
                            .createdBy(userId)
                            .items(request.getItems().stream().map(i -> 
                                StockMovementItem.builder()
                                    .productId(i.getProductId())
                                    .quantity(i.getQuantity())
                                    .build()
                            ).collect(Collectors.toList()))
                            .build();
                    
                    return movementService.createDraft(movement);
                }))
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m));
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Valider un mouvement")
    @PreAuthorize("hasAuthority('MOVEMENT:VALIDATE')")
    public Mono<ResponseEntity<StockMovement>> validateMovement(@PathVariable UUID id) {
        return securityUtils.getCurrentUserId()
                .flatMap(validatorId -> movementService.validateMovement(id, validatorId))
                .map(ResponseEntity::ok);
    }

    @Data
    public static class CreateMovementRequest {
        @Schema(example = "IN", description = "IN, OUT, TRANSFER")
        private String type;
        private UUID sourceAgencyId;
        private UUID destinationAgencyId;
        private UUID thirdPartyId;
        private String notes;
        private List<CreateItemRequest> items;
    }

    @Data
    public static class CreateItemRequest {
        private UUID productId;
        private Integer quantity;
    }
}