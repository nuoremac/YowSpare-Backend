package yowyob.comops.spareapi.workflow.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;
import yowyob.comops.spareapi.workflow.dto.CreateWorkflowRequest;
import yowyob.comops.spareapi.workflow.dto.WorkflowRequestDto;
import yowyob.comops.spareapi.workflow.service.WorkflowService;

import java.util.UUID;

@RestController
@RequestMapping("/workflow/requests")
public class WorkflowController {
    private final WorkflowService service;

    public WorkflowController(WorkflowService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<WorkflowRequestDto> list(@RequestParam(required = false) String status,
                                         @RequestParam(required = false) String type,
                                         ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.search(tenantId, status, type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WorkflowRequestDto> create(@Valid @RequestBody CreateWorkflowRequest req, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser().defaultIfEmpty("unknown")
                .flatMap(userId -> service.create(tenantId, userId, req));
    }

    @PostMapping("/{id}/approve")
    public Mono<WorkflowRequestDto> approve(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser().defaultIfEmpty("unknown")
                .flatMap(userId -> service.approve(tenantId, id, userId));
    }

    @PostMapping("/{id}/reject")
    public Mono<WorkflowRequestDto> reject(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser().defaultIfEmpty("unknown")
                .flatMap(userId -> service.reject(tenantId, id, userId));
    }

    private Mono<String> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication() == null ? null : ctx.getAuthentication().getName());
    }
}

