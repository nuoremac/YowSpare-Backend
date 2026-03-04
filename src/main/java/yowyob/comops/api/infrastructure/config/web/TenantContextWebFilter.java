package yowyob.comops.api.infrastructure.config.web;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import java.util.UUID;

@Component
public class TenantContextWebFilter implements WebFilter {
    public static final String TENANT_HEADER = "X-Tenant-ID";
    public static final String TENANT_CONTEXT_KEY = "TENANT_ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String tenantIdHeader = exchange.getRequest().getHeaders().getFirst(TENANT_HEADER);

        if (tenantIdHeader != null && !tenantIdHeader.isBlank()) {
            try {
                UUID tenantId = UUID.fromString(tenantIdHeader);
                // On ajoute l'ID au contexte réactif pour qu'il soit accessible plus tard
                return chain.filter(exchange)
                        .contextWrite(Context.of(TENANT_CONTEXT_KEY, tenantId));
            } catch (IllegalArgumentException e) {
                // Header invalide, on ignore (sera géré par le fallback ou erreur 400 plus
                // tard)
                return chain.filter(exchange);
            }
        }

        return chain.filter(exchange);
    }
}