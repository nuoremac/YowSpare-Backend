package yowyob.comops.spareapi.supplier.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;
import yowyob.comops.spareapi.supplier.dto.*;
import yowyob.comops.spareapi.supplier.service.SupplierService;

import java.util.UUID;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<SupplierDto> list(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) String status,
                                  ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.search(tenantId, q, status);
    }

    @GetMapping("/{id}")
    public Mono<SupplierDto> get(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.get(tenantId, id);
    }

    @PutMapping("/{id}")
    public Mono<SupplierDto> upsert(@PathVariable UUID id,
                                    @Valid @RequestBody UpsertSupplierRequest req,
                                    ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.upsert(tenantId, id, req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SupplierDto> create(@Valid @RequestBody UpsertSupplierRequest req, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.upsert(tenantId, UUID.randomUUID(), req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.delete(tenantId, id);
    }

    @GetMapping("/{supplierId}/products")
    public Flux<SupplierProductDto> listSupplierProducts(@PathVariable UUID supplierId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listSupplierProducts(tenantId, supplierId);
    }

    @PutMapping("/{supplierId}/products/{productId}")
    public Mono<SupplierProductDto> upsertSupplierProduct(@PathVariable UUID supplierId,
                                                          @PathVariable UUID productId,
                                                          @Valid @RequestBody UpsertSupplierProductRequest req,
                                                          ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.upsertSupplierProduct(tenantId, supplierId, productId, req);
    }

    @DeleteMapping("/{supplierId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSupplierProduct(@PathVariable UUID supplierId,
                                            @PathVariable UUID productId,
                                            ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.deleteSupplierProduct(tenantId, supplierId, productId);
    }

    @GetMapping("/products/{productId}")
    public Flux<SupplierProductDto> listByProduct(@PathVariable UUID productId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listByProduct(tenantId, productId);
    }
}

