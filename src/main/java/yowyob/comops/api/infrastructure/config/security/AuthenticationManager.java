package yowyob.comops.api.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRoleRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final R2dbcUserRepository userRepository;
    private final R2dbcUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String email = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .next() // Prend le premier qui correspond
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid Credentials")))
                .flatMap(user -> userRoleRepository.findRolesByUserId(user.getId())
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collectList()
                        .map(authorities -> {
                            // Le Principal est construit avec l'UUID de l'utilisateur comme "username"
                            org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                                    user.getId().toString(),
                                    user.getPasswordHash(),
                                    user.isActive(),
                                    true, true, true,
                                    authorities);
                            return new UsernamePasswordAuthenticationToken(principal, null, authorities);
                        }));
    }
}