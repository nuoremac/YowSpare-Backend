package yowyob.comops.api.infrastructure.adapter.in.web.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import yowyob.comops.api.domain.model.security.SystemAudit;
import yowyob.comops.api.domain.service.security.SystemAuditService;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;

@RestController
@RequestMapping("/system-audits")
@RequiredArgsConstructor
@Tag(name = "System Audits", description = "Consultation des journaux d'activité")
public class SystemAuditController {
    private final SystemAuditService auditService;
    private final SecurityUtils securityUtils;

    @GetMapping("/me")
    @Operation(summary = "Consulter mon activité récente", description = "Récupère les dernières actions effectuées par l'utilisateur connecté.")
    public Flux<SystemAudit> getMyActivity(
            @Parameter(description = "Nombre maximum d'entrées à retourner") @RequestParam(defaultValue = "50") int limit) {
        return securityUtils.getCurrentUserId()
                .flatMapMany(userId -> auditService.getUserActivity(userId, limit));
    }

    @GetMapping("/organization")
    @Operation(summary = "Consulter l'activité de l'organisation", description = "Récupère les dernières actions effectuées au sein de l'organisation courante (définie par X-Tenant-ID).")
    public Flux<SystemAudit> getOrganizationActivity(
            @Parameter(description = "Nombre maximum d'entrées à retourner") @RequestParam(defaultValue = "50") int limit) {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(orgId -> auditService.getOrganizationActivity(orgId, limit));
    }
}