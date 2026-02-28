package yowyob.comops.spareapi.warehouse.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;
import yowyob.comops.spareapi.warehouse.dto.ProductLocationDto;
import yowyob.comops.spareapi.warehouse.dto.UpsertProductLocationRequest;
import yowyob.comops.spareapi.warehouse.dto.WarehouseLayoutDto;
import yowyob.comops.spareapi.warehouse.service.WarehouseService;

import java.util.UUID;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/{agencyId}/layout")
    public Mono<WarehouseLayoutDto> getLayout(@PathVariable UUID agencyId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return warehouseService.getLayout(tenantId, agencyId);
    }

    @PutMapping("/{agencyId}/layout")
    public Mono<WarehouseLayoutDto> putLayout(@PathVariable UUID agencyId,
                                              @Valid @RequestBody WarehouseLayoutDto req,
                                              ServerWebExchange exchange) {
        if (req.agencyId() != null && !req.agencyId().equals(agencyId)) {
            return Mono.error(new IllegalArgumentException("agencyId mismatch."));
        }
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        WarehouseLayoutDto normalized = new WarehouseLayoutDto(agencyId, req.type(), req.width(), req.height(), req.layout());
        return warehouseService.upsertLayout(tenantId, agencyId, normalized);
    }

    @GetMapping("/{agencyId}/product-locations")
    public Flux<ProductLocationDto> listLocations(@PathVariable UUID agencyId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return warehouseService.listProductLocations(tenantId, agencyId);
    }

    @PutMapping("/{agencyId}/product-locations/{productId}")
    public Mono<ProductLocationDto> upsertLocation(@PathVariable UUID agencyId,
                                                   @PathVariable UUID productId,
                                                   @Valid @RequestBody UpsertProductLocationRequest req,
                                                   ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return warehouseService.upsertProductLocation(tenantId, agencyId, productId, req);
    }

    @GetMapping("/products/{productId}/locations")
    public Flux<ProductLocationDto> listLocationsForProduct(@PathVariable UUID productId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return warehouseService.listLocationsForProduct(tenantId, productId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> badRequest(IllegalArgumentException e) {
        return Mono.just(e.getMessage() == null ? "Bad request" : e.getMessage());
    }
}

