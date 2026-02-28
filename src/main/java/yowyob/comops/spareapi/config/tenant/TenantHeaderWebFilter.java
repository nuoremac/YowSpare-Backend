package yowyob.comops.spareapi.config.tenant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class TenantHeaderWebFilter implements WebFilter {
    public static final String TENANT_ID_ATTR = "tenantId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/actuator") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")
                || path.startsWith("/webjars")) {
            return chain.filter(exchange);
        }

        // Tenant header is required for all business endpoints.
        String tenantHeader = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");
        if (tenantHeader == null || tenantHeader.isBlank()) {
            return writeJsonError(exchange, HttpStatus.BAD_REQUEST, "Missing X-Tenant-ID header.");
        }
        try {
            UUID tenantId = UUID.fromString(tenantHeader.trim());
            exchange.getAttributes().put(TENANT_ID_ATTR, tenantId);
            return chain.filter(exchange);
        } catch (IllegalArgumentException e) {
            return writeJsonError(exchange, HttpStatus.BAD_REQUEST, "Invalid X-Tenant-ID header.");
        }
    }

    private Mono<Void> writeJsonError(ServerWebExchange exchange, HttpStatus status, String message) {
        byte[] bytes = ("{\"status\":" + status.value() + ",\"error\":\"" + status.getReasonPhrase() + "\",\"message\":\""
                + escape(message) + "\",\"path\":\"" + escape(exchange.getRequest().getPath().value()) + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().set(HttpHeaders.CACHE_CONTROL, "no-store");
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
