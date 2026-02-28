package yowyob.comops.spareapi.policy.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;
import yowyob.comops.spareapi.policy.dto.LocationPolicyDto;
import yowyob.comops.spareapi.policy.dto.UpsertLocationPolicyRequest;
import yowyob.comops.spareapi.policy.service.LocationPolicyService;

import java.util.UUID;

@RestController
@RequestMapping("/warehouses")
public class LocationPolicyController {
    private final LocationPolicyService service;

    public LocationPolicyController(LocationPolicyService service) {
        this.service = service;
    }

    @GetMapping("/{agencyId}/bins/{binCode}/policies")
    public Flux<LocationPolicyDto> listForBin(@PathVariable UUID agencyId,
                                              @PathVariable String binCode,
                                              @RequestParam(required = false) UUID productId,
                                              ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        if (productId != null) {
            return service.listForBinAndProduct(tenantId, agencyId, binCode, productId);
        }
        return service.listForBin(tenantId, agencyId, binCode);
    }

    @PutMapping("/{agencyId}/bins/{binCode}/policies")
    public Mono<LocationPolicyDto> upsert(@PathVariable UUID agencyId,
                                          @PathVariable String binCode,
                                          @RequestParam(required = false) UUID productId,
                                          @Valid @RequestBody UpsertLocationPolicyRequest req,
                                          ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.upsert(tenantId, agencyId, binCode, productId, req);
    }

    @DeleteMapping("/{agencyId}/bins/{binCode}/policies")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID agencyId,
                             @PathVariable String binCode,
                             @RequestParam(required = false) UUID productId,
                             ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.delete(tenantId, agencyId, binCode, productId);
    }

    @GetMapping("/{agencyId}/policies")
    public Flux<LocationPolicyDto> listForAgency(@PathVariable UUID agencyId,
                                                 @RequestParam(required = false) UUID productId,
                                                 ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listForAgency(tenantId, agencyId, productId);
    }
}

