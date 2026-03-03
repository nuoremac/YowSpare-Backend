package yowyob.comops.stock.api.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.infrastructure.security.UserContext;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    public Mono<UserContext> getUserContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(UserContext.class);
    }

    public Mono<UUID> getCurrentUserId() {
        return getUserContext().map(UserContext::getUserId);
    }

    public Mono<UUID> getCurrentOrganizationId() {
        return getUserContext().map(UserContext::getOrganizationId);
    }
    
    /**
     * Retourne l'ID de l'agence de l'utilisateur.
     * Retourne Mono.empty() si l'utilisateur est un staff global (Siège).
     */
    public Mono<UUID> getCurrentAgencyId() {
        return getUserContext()
                .map(ctx -> ctx.getAgencyId() != null ? ctx.getAgencyId() : null)
                .onErrorResume(e -> Mono.empty()); // Sécurité si null
    }

    /**
     * Vérifie si l'utilisateur est un Admin/Staff Global (Siège).
     */
    public Mono<Boolean> isGlobalStaff() {
        return getUserContext()
                .map(ctx -> ctx.getAgencyId() == null);
    }
}