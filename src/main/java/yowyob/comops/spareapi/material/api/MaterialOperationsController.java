package yowyob.comops.spareapi.material.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.config.tenant.TenantHeaderWebFilter;
import yowyob.comops.spareapi.material.dto.*;
import yowyob.comops.spareapi.material.service.MaterialOperationsService;

import java.util.UUID;

@RestController
public class MaterialOperationsController {
    private final MaterialOperationsService service;

    public MaterialOperationsController(MaterialOperationsService service) {
        this.service = service;
    }

    @GetMapping("/departments")
    public Flux<DepartmentDto> listDepartments(@RequestParam(required = false) UUID agencyId,
                                               @RequestParam(required = false) Boolean active,
                                               ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listDepartments(tenantId, agencyId, active);
    }

    @PostMapping("/departments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DepartmentDto> createDepartment(@Valid @RequestBody CreateDepartmentRequest req,
                                                ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.createDepartment(tenantId, req);
    }

    @PatchMapping("/departments/{id}")
    public Mono<DepartmentDto> updateDepartment(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateDepartmentRequest req,
                                                ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.updateDepartment(tenantId, id, req);
    }

    @DeleteMapping("/departments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDepartment(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.deleteDepartment(tenantId, id);
    }

    @GetMapping("/departments/{id}/members")
    public Flux<DepartmentMemberDto> listDepartmentMembers(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listDepartmentMembers(tenantId, id);
    }

    @PostMapping("/departments/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DepartmentMemberDto> addDepartmentMember(@PathVariable UUID id,
                                                          @Valid @RequestBody CreateDepartmentMemberRequest req,
                                                          ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.addDepartmentMember(tenantId, id, req);
    }

    @DeleteMapping("/departments/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeDepartmentMember(@PathVariable UUID id,
                                             @PathVariable String userId,
                                             ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.removeDepartmentMember(tenantId, id, userId);
    }

    @GetMapping("/material-requests")
    public Flux<MaterialRequestDto> listMaterialRequests(@RequestParam(required = false) UUID agencyId,
                                                         @RequestParam(required = false) UUID departmentId,
                                                         @RequestParam(required = false) String status,
                                                         ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listRequests(tenantId, agencyId, departmentId, status);
    }

    @GetMapping("/material-requests/{id}")
    public Mono<MaterialRequestDto> getMaterialRequest(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.getRequest(tenantId, id);
    }

    @PostMapping("/material-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MaterialRequestDto> createMaterialRequest(@Valid @RequestBody CreateMaterialRequestRequest req,
                                                           ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(actorId -> service.createRequest(tenantId, actorId, req));
    }

    @PostMapping("/material-requests/{id}/approve")
    public Mono<MaterialRequestDto> approve(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(actorId -> service.approveRequest(tenantId, id, actorId));
    }

    @PostMapping("/material-requests/{id}/reject")
    public Mono<MaterialRequestDto> reject(@PathVariable UUID id,
                                           @RequestBody(required = false) RejectMaterialRequestRequest req,
                                           ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(actorId -> service.rejectRequest(tenantId, id, actorId, req));
    }

    @PostMapping("/material-requests/{id}/issue")
    public Mono<MaterialRequestDto> issue(@PathVariable UUID id,
                                          @Valid @RequestBody MaterialActionRequest req,
                                          ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(actorId -> service.issue(tenantId, id, actorId, req));
    }

    @PostMapping("/material-requests/{id}/return")
    public Mono<MaterialRequestDto> registerReturn(@PathVariable UUID id,
                                                   @Valid @RequestBody MaterialActionRequest req,
                                                   ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(actorId -> service.registerReturn(tenantId, id, actorId, req));
    }

    @PostMapping("/material-requests/{id}/close")
    public Mono<MaterialRequestDto> close(@PathVariable UUID id,
                                          @RequestBody(required = false) CloseMaterialRequestRequest req,
                                          ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return currentUser()
                .defaultIfEmpty("unknown")
                .flatMap(actorId -> service.closeRequest(tenantId, id, actorId, req));
    }

    @GetMapping("/material-requests/{id}/trace-events")
    public Flux<TraceEventDto> listTraceEvents(@PathVariable UUID id, ServerWebExchange exchange) {
        UUID tenantId = (UUID) exchange.getAttribute(TenantHeaderWebFilter.TENANT_ID_ATTR);
        return service.listTraceEventsForRequest(tenantId, id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> badRequest(IllegalArgumentException e) {
        return Mono.just(e.getMessage() == null ? "Bad request" : e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<String> conflict(IllegalStateException e) {
        return Mono.just(e.getMessage() == null ? "Conflict" : e.getMessage());
    }

    private Mono<String> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication() == null ? null : ctx.getAuthentication().getName());
    }
}
