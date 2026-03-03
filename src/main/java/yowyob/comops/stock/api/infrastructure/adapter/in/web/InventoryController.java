package yowyob.comops.stock.api.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
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
import yowyob.comops.stock.api.domain.model.InventoryCount;
import yowyob.comops.stock.api.domain.model.InventorySession;
import yowyob.comops.stock.api.domain.port.in.InventoryUseCase;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Sessions d'inventaire et ajustements de stock")
public class InventoryController {
    private final InventoryUseCase inventoryService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Lister les inventaires de mon agence")
    @PreAuthorize("hasAuthority('INVENTORY:INITIATE')") // On peut considérer un droit INVENTORY:READ distinct
    public Flux<InventorySession> getMyInventories() {
        return securityUtils.getCurrentAgencyId()
                .flux()
                .flatMap(inventoryService::getSessionsByAgency);
    }

    @PostMapping
    @Operation(summary = "Ouvrir une nouvelle session d'inventaire")
    @ApiResponse(responseCode = "201", description = "Session créée avec le snapshot du stock théorique")
    @PreAuthorize("hasAuthority('INVENTORY:INITIATE')")
    public Mono<ResponseEntity<InventorySession>> initiate(@RequestBody InitiateInventoryRequest request) {
        return securityUtils.getUserContext()
                .flatMap(ctx -> {
                    if (ctx.getAgencyId() == null) {
                        return Mono.error(new IllegalStateException("Inventory can only be initiated by agency staff."));
                    }
                    InventorySession session = InventorySession.builder()
                            .organizationId(ctx.getOrganizationId())
                            .agencyId(ctx.getAgencyId())
                            .description(request.getDescription())
                            .categoryIdScope(request.getCategoryIdScope())
                            .build();
                    return inventoryService.initiateSession(session);
                })
                .map(createdSession -> ResponseEntity.status(HttpStatus.CREATED).body(createdSession));
    }

    @PostMapping("/{id}/counts")
    @Operation(summary = "Soumettre les comptages")
    @ApiResponse(responseCode = "200", description = "Comptages enregistrés")
    @PreAuthorize("hasAuthority('INVENTORY:COUNT')")
    public Mono<ResponseEntity<Void>> submitCounts(@PathVariable UUID id, @RequestBody List<CountRequest> counts) {
        List<InventoryCount> domainCounts = counts.stream()
                .map(c -> InventoryCount.builder()
                        .productId(c.getProductId())
                        .physicalQuantity(c.getQuantity())
                        .build())
                .collect(Collectors.toList());
        
        return inventoryService.submitCounts(id, domainCounts)
                .map(v -> ResponseEntity.ok().build());
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Valider l'inventaire et ajuster le stock")
    @PreAuthorize("hasAuthority('INVENTORY:VALIDATE')")
    public Mono<ResponseEntity<InventorySession>> validate(@PathVariable UUID id) {
        return securityUtils.getCurrentUserId()
                .flatMap(userId -> inventoryService.validateSession(id, userId))
                .map(ResponseEntity::ok);
    }

    @Data
    static class InitiateInventoryRequest {
        private String description;
        private UUID categoryIdScope;
    }

    @Data
    static class CountRequest {
        private UUID productId;
        private Integer quantity;
    }
}