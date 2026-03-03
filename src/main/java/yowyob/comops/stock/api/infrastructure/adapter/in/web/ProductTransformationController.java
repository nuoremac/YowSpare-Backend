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
import yowyob.comops.stock.api.domain.model.ProductTransformation;
import yowyob.comops.stock.api.domain.model.TransformationItem;
import yowyob.comops.stock.api.domain.port.in.ProductTransformationUseCase;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product-transformations")
@RequiredArgsConstructor
@Tag(name = "Manufacturing", description = "Fabrication et Transformation")
public class ProductTransformationController {

    private final ProductTransformationUseCase service;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Lister les ordres de fabrication")
    @PreAuthorize("hasAuthority('MANUFACTURING:MANAGE')")
    public Flux<ProductTransformation> getAll() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(service::getAll);
    }

    @PostMapping
    @Operation(summary = "Créer un ordre de fabrication (Brouillon)")
    @ApiResponse(responseCode = "201", description = "Ordre de fabrication créé")
    @PreAuthorize("hasAuthority('MANUFACTURING:MANAGE')")
    public Mono<ResponseEntity<ProductTransformation>> create(@RequestBody TransformationRequest request) {
        return securityUtils.getUserContext()
                .flatMap(ctx -> {
                    if (ctx.getAgencyId() == null) {
                        return Mono.error(new IllegalStateException("Manufacturing must be initiated from a specific agency."));
                    }
                    ProductTransformation pt = ProductTransformation.builder()
                            .organizationId(ctx.getOrganizationId())
                            .agencyId(ctx.getAgencyId())
                            .description(request.getDescription())
                            .inputs(mapItems(request.getInputs()))
                            .outputs(mapItems(request.getOutputs()))
                            .build();
                    return service.create(pt);
                })
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Valider et exécuter la fabrication")
    @PreAuthorize("hasAuthority('MANUFACTURING:MANAGE')")
    public Mono<ResponseEntity<ProductTransformation>> validate(@PathVariable UUID id) {
        return securityUtils.getCurrentUserId()
                .flatMap(userId -> service.validate(id, userId))
                .map(ResponseEntity::ok);
    }

    private List<TransformationItem> mapItems(List<ItemRequest> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(i -> TransformationItem.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Data
    static class TransformationRequest {
        private String description;
        private List<ItemRequest> inputs;
        private List<ItemRequest> outputs;
    }

    @Data
    static class ItemRequest {
        private UUID productId;
        private Integer quantity;
    }
}