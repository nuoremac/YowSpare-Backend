package yowyob.comops.spareapi.config.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class BearerSecurityContextRepository implements ServerSecurityContextRepository {
    private final JwtUtil jwtUtil;

    public BearerSecurityContextRepository(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return Mono.empty();

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) return Mono.empty();

        Claims claims = jwtUtil.parseClaims(token);
        String subject = claims.getSubject();
        Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
        return Mono.just(new SecurityContextImpl(auth));
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object raw = claims.get("roles");
        if (raw == null) return List.of();

        List<GrantedAuthority> out = new ArrayList<>();

        // In core-api tokens, roles are stored as an array of objects like { "authority": "ROLE_ADMIN" }.
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> m) {
                    Object auth = m.get("authority");
                    if (auth != null) out.add(new SimpleGrantedAuthority(String.valueOf(auth)));
                } else if (item instanceof String s) {
                    out.add(new SimpleGrantedAuthority(s));
                }
            }
        }
        return out;
    }
}

