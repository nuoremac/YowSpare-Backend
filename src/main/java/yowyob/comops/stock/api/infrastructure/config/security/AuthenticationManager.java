package yowyob.comops.stock.api.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.infrastructure.security.RemoteAuthService;
import yowyob.comops.stock.api.infrastructure.security.UserContext;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final RemoteAuthService remoteAuthService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        // Le principal contient le TenantID passé par le SecurityContextRepository
        String tenantId = (String) authentication.getPrincipal(); 

        return remoteAuthService.validateAndGetContext(token, tenantId)
                .map(context -> {
                    List<SimpleGrantedAuthority> authorities = context.getPermissions().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // Le Principal de l'authentification est notre objet UserContext
                    return new UsernamePasswordAuthenticationToken(
                            context,
                            token,
                            authorities
                    );
                });
    }
}