package yowyob.comops.api.infrastructure.adapter.in.web.internal;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationMemberRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcPermissionRepository;
import yowyob.comops.api.infrastructure.config.security.JwtUtil;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
@Hidden // Ne pas exposer dans le Swagger public
public class InternalAuthController {

    private final JwtUtil jwtUtil;
    private final SecurityUtils securityUtils;
    private final R2dbcOrganizationMemberRepository memberRepository;
    private final R2dbcPermissionRepository permissionRepository;

    @PostMapping("/validate")
    public Mono<ResponseEntity<ValidationResponse>> validateAccess(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("X-Tenant-ID") String tenantIdStr) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        UUID tenantId;
        try {
            tenantId = UUID.fromString(tenantIdStr);
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        // 1. Validation technique du Token
        if (!jwtUtil.validateToken(token)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        String userIdStr = jwtUtil.extractUsername(token);
        UUID userId = UUID.fromString(userIdStr);
        Date expiration = jwtUtil.extractExpiration(token);
        long secondsRemaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;

        if (secondsRemaining <= 0) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        // 2. Validation fonctionnelle (Appartenance au Tenant + Rôles)
        // On cherche le membre
        return memberRepository.findByOrganizationIdAndUserId(tenantId, userId)
                .flatMap(member -> {
                    // Récupérer les permissions du rôle
                    return permissionRepository.findByRoleId(member.getRoleId())
                            .map(perm -> perm.getResource() + ":" + perm.getAction())
                            .collectList()
                            .map(permissions -> {
                                ValidationResponse response = new ValidationResponse();
                                response.setUserId(userId);
                                response.setOrganizationId(tenantId);
                                response.setAgencyId(member.getAgencyId()); // Important pour le stock !
                                response.setPermissions(permissions);
                                response.setExpiresInSeconds(secondsRemaining);
                                return ResponseEntity.ok(response);
                            });
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build())); // Pas membre de l'org
    }

    @Data
    public static class ValidationResponse {
        private UUID userId;
        private UUID organizationId;
        private UUID agencyId;
        private List<String> permissions;
        private long expiresInSeconds;
    }
}