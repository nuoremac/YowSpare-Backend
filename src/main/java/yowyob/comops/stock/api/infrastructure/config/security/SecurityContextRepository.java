package yowyob.comops.stock.api.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String tenantHeader = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");

        if (authHeader != null && authHeader.startsWith("Bearer ") && tenantHeader != null) {
            String token = authHeader.substring(7);
            
            // On passe (TenantID, Token) à l'AuthManager
            // Note: On utilise TenantID comme "Principal" temporaire et Token comme "Credentials"
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(tenantHeader, token);
            
            return authenticationManager.authenticate(auth)
                    .map(SecurityContextImpl::new);
        }
        return Mono.empty();
    }
}