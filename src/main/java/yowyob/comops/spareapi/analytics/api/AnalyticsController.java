package yowyob.comops.spareapi.analytics.api;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import yowyob.comops.spareapi.analytics.dto.AnalyticsRecommendationDto;
import yowyob.comops.spareapi.analytics.service.AnalyticsService;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;

import java.util.UUID;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/reorder-recommendations")
    public Flux<AnalyticsRecommendationDto> list(@RequestParam UUID agencyId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.list(tenantId, agencyId);
    }

    @PostMapping("/reorder-recommendations/recompute")
    public Flux<AnalyticsRecommendationDto> recompute(@RequestParam UUID agencyId,
                                                      @RequestParam(defaultValue = "30") int days,
                                                      ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String tenantHeader = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Flux.error(new IllegalArgumentException("Missing Authorization header."));
        }
        if (tenantHeader == null || tenantHeader.isBlank()) {
            return Flux.error(new IllegalArgumentException("Missing X-Tenant-ID header."));
        }
        return service.recompute(tenantId, agencyId, authorization, tenantHeader, days);
    }
}
