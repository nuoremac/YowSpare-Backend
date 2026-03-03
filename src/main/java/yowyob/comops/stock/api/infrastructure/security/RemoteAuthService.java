package yowyob.comops.stock.api.infrastructure.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteAuthService {

    private final ReactiveRedisTemplate<String, UserContext> redisTemplate;
    
    @Value("${app.core-api.url}")
    private String coreApiUrl;
    
    @Value("${app.auth.cache-ttl-max}")
    private long maxCacheTtl;

    public Mono<UserContext> validateAndGetContext(String token, String tenantId) {
        String cacheKey = "auth:" + token + ":" + tenantId;

        return redisTemplate.opsForValue().get(cacheKey)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for token/tenant. Calling Core API.");
                    WebClient client = WebClient.create(coreApiUrl);
                    
                    return client.post()
                            .uri("/internal/auth/validate")
                            .header("Authorization", "Bearer " + token)
                            .header("X-Tenant-ID", tenantId)
                            .retrieve()
                            .bodyToMono(CoreValidationResponse.class)
                            .flatMap(response -> {
                                UserContext context = UserContext.builder()
                                        .userId(response.getUserId())
                                        .organizationId(response.getOrganizationId())
                                        .agencyId(response.getAgencyId())
                                        .permissions(response.getPermissions())
                                        .build();

                                // Utiliser le TTL le plus court entre l'expiration du token et notre max config
                                long ttl = Math.min(response.getExpiresInSeconds(), maxCacheTtl);
                                
                                log.info("Caching user context for {} seconds.", ttl);
                                return redisTemplate.opsForValue()
                                        .set(cacheKey, context, Duration.ofSeconds(ttl))
                                        .thenReturn(context);
                            });
                }));
    }

    @Data
    static class CoreValidationResponse {
        private UUID userId;
        private UUID organizationId;
        private UUID agencyId;
        private List<String> permissions;
        private long expiresInSeconds;
    }
}