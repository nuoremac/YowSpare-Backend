package yowyob.comops.api.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationMemberRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcPermissionRepository;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RbacEvaluator {
    private final SecurityUtils securityUtils;
    private final R2dbcOrganizationRepository orgRepository;
    private final R2dbcOrganizationMemberRepository memberRepository;
    private final R2dbcPermissionRepository permissionRepository;

    public Mono<Boolean> isOwner() {
        return Mono.zip(securityUtils.getCurrentOrganizationId(), securityUtils.getCurrentUser())
                .flatMap(tuple -> {
                    UUID orgId = tuple.getT1();
                    UUID businessActorId = tuple.getT2().getBusinessActorId();
                    if (businessActorId == null)
                        return Mono.just(false);
                    return orgRepository.existsByIdAndBusinessActorId(orgId, businessActorId);
                })
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> hasPermission(String resource, String action) {
        return isOwner().flatMap(isOwner -> {
            if (Boolean.TRUE.equals(isOwner))
                return Mono.just(true);
            return Mono.zip(securityUtils.getCurrentOrganizationId(), securityUtils.getCurrentUserId())
                    .flatMap(tuple -> memberRepository.findByOrganizationIdAndUserId(tuple.getT1(), tuple.getT2()))
                    .flatMap(member -> hasPermission(member.getRoleId(), resource, action));
        });
    }

    public Mono<Boolean> hasPermission(UUID roleId, String resource, String action) {
        return permissionRepository.findByRoleId(roleId)
                .any(p -> p.getResource().equals(resource) && p.getAction().equals(action));
    }

    public Mono<Boolean> canManageStaffForAgency(UUID targetAgencyId) {
        return isOwner().flatMap(isOwner -> {
            if (Boolean.TRUE.equals(isOwner)) {
                return Mono.just(true);
            }

            return Mono.zip(securityUtils.getCurrentOrganizationId(), securityUtils.getCurrentUserId())
                    .flatMap(tuple -> memberRepository.findByOrganizationIdAndUserId(tuple.getT1(), tuple.getT2()))
                    .flatMap(member -> {
                        // Cas 1: Staff Global (Siège)
                        if (member.getAgencyId() == null) {
                            // S'il a le droit de gérer globalement, il peut gérer n'importe quelle agence
                            // (y compris null)
                            return hasPermission(member.getRoleId(), "HR", "MANAGE_GLOBAL");
                        }

                        // Cas 2: Staff Local - Doit matcher l'agence cible (si elle existe)
                        if (targetAgencyId != null && member.getAgencyId().equals(targetAgencyId)) {
                            return hasPermission(member.getRoleId(), "HR", "MANAGE_LOCAL");
                        }

                        // Cas 3: Staff Local essaie de créer un admin global (targetAgencyId = null)
                        if (targetAgencyId == null) {
                            return Mono.just(false); // Un manager local ne peut pas créer un admin global
                        }

                        return Mono.just(false);
                    })
                    .defaultIfEmpty(false); // Si pas membre, pas de droits
        });
    }
}