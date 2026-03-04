package yowyob.comops.api.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.UserEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationMemberRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import yowyob.comops.api.infrastructure.config.web.TenantContextWebFilter;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final R2dbcUserRepository userRepository;
    private final R2dbcOrganizationRepository organizationRepository;
    private final R2dbcOrganizationMemberRepository memberRepository;

    /**
     * Récupère l'utilisateur connecté depuis la base de données.
     * Utilise l'ID stocké dans le Principal pour garantir l'unicité.
     */
    public Mono<UserEntity> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> {
                    Object principal = authentication.getPrincipal();

                    if (principal instanceof UserDetails) {
                        String userIdString = ((UserDetails) principal).getUsername();
                        try {
                            UUID userId = UUID.fromString(userIdString);
                            return userRepository.findById(userId);
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid User UUID in security context: {}", userIdString);
                            return Mono.empty();
                        }
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.error(new IllegalStateException("No authenticated user found")));
    }

    public Mono<UUID> getCurrentUserId() {
        return getCurrentUser().map(UserEntity::getId);
    }

    /**
     * Récupère l'ID de l'organisation courante.
     * Algorithme :
     * 1. Cherche l'ID dans le contexte Reactor (placé par TenantContextWebFilter
     * via le header X-Tenant-ID).
     * 2. Si présent : Vérifie que l'utilisateur a accès (Owner ou Membre).
     * 3. Si absent ou accès refusé : Cherche une organisation par défaut (Première
     * trouvée).
     */
    public Mono<UUID> getCurrentOrganizationId() {
        return Mono.deferContextual(ctx -> {
            // 1. Tenter de récupérer le Tenant ID du contexte (fourni par le header HTTP)
            if (ctx.hasKey(TenantContextWebFilter.TENANT_CONTEXT_KEY)) {
                UUID requestedTenantId = ctx.get(TenantContextWebFilter.TENANT_CONTEXT_KEY);
                return verifyAndReturnTenant(requestedTenantId);
            } else {
                // 2. Fallback : Pas de header, on cherche une organisation par défaut pour cet
                // utilisateur
                return findDefaultOrganization();
            }
        });
    }

    /**
     * Vérifie si l'utilisateur courant a le droit d'accéder à l'organisation
     * demandée.
     */
    private Mono<UUID> verifyAndReturnTenant(UUID tenantId) {
        return getCurrentUser().flatMap(user -> {
            // A. Vérifier si propriétaire (Owner) via le BusinessActor
            Mono<Boolean> isOwner = Mono.just(false);
            if (user.getBusinessActorId() != null) {
                isOwner = organizationRepository.existsByIdAndBusinessActorId(tenantId, user.getBusinessActorId());
            }

            return isOwner.flatMap(owner -> {
                if (Boolean.TRUE.equals(owner)) {
                    return Mono.just(tenantId);
                }

                // B. Vérifier si membre actif (Employee)
                return memberRepository.existsByOrganizationIdAndUserId(tenantId, user.getId())
                        .flatMap(isMember -> {
                            if (Boolean.TRUE.equals(isMember)) {
                                return Mono.just(tenantId);
                            }
                            return Mono.error(new IllegalStateException("Access denied to organization " + tenantId));
                        });
            });
        });
    }

    /**
     * Trouve une organisation par défaut si aucune n'est spécifiée dans la requête.
     * Priorité : Organisation possédée > Organisation membre.
     */
    private Mono<UUID> findDefaultOrganization() {
        return getCurrentUser().flatMap(user -> {
            // Priorité 1 : Organisations dont je suis propriétaire
            if (user.getBusinessActorId() != null) {
                return organizationRepository.findByBusinessActorId(user.getBusinessActorId())
                        .next() // Prend la première trouvée
                        .map(org -> org.getId())
                        // Si je suis BusinessActor mais sans organisation (cas rare), on cherche les
                        // memberships
                        .switchIfEmpty(findFirstMembership(user.getId()));
            }
            // Priorité 2 : Organisations dont je suis membre
            return findFirstMembership(user.getId());
        }).switchIfEmpty(Mono.error(new IllegalStateException(
                "User is not associated with any organization. Please create one or accept an invitation.")));
    }

    private Mono<UUID> findFirstMembership(UUID userId) {
        return memberRepository.findByUserId(userId)
                .filter(m -> m.isActive())
                .next()
                .map(m -> m.getOrganizationId());
    }
}