package yowyob.comops.spareapi.reservation.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;
import yowyob.comops.spareapi.reservation.dto.*;
import yowyob.comops.spareapi.reservation.service.ReservationService;

import java.util.List;
import java.util.UUID;

@RestController
public class ReservationController {
    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ReservationDto> create(@Valid @RequestBody CreateReservationRequest req, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(userId -> service.create(tenantId, userId, req));
    }

    @GetMapping("/reservations")
    public Flux<ReservationDto> list(@RequestParam(required = false) UUID agencyId,
                                     @RequestParam(required = false) UUID productId,
                                     @RequestParam(required = false) String status,
                                     ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.search(tenantId, agencyId, productId, status);
    }

    @PatchMapping("/reservations/{id}")
    public Mono<ReservationDto> updateStatus(@PathVariable UUID id,
                                             @Valid @RequestBody UpdateReservationStatusRequest req,
                                             ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.updateStatus(tenantId, id, req.status());
    }

    @GetMapping("/availability")
    public Mono<List<AvailabilityRowDto>> availability(@RequestParam UUID agencyId, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String tenantHeader = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Mono.error(new IllegalArgumentException("Missing Authorization header."));
        }
        if (tenantHeader == null || tenantHeader.isBlank()) {
            return Mono.error(new IllegalArgumentException("Missing X-Tenant-ID header."));
        }
        return service.availability(tenantId, agencyId, authorization, tenantHeader);
    }

    private Mono<String> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication() == null ? null : ctx.getAuthentication().getName());
    }
}

