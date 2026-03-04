package yowyob.comops.api.infrastructure.adapter.out.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRoleRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final R2dbcUserRepository userRepository;
    private final R2dbcUserRoleRepository userRoleRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // "username" est l'UUID de l'utilisateur, venant du token JWT
        UUID userId;
        try {
            userId = UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            return Mono.error(new UsernameNotFoundException("Invalid User ID format"));
        }

        return userRepository.findById(userId)
                .flatMap(user -> userRoleRepository.findRolesByUserId(user.getId())
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collectList()
                        .map(authorities -> org.springframework.security.core.userdetails.User
                                // CORRECTION : Le "username" du principal DOIT être l'UUID
                                .withUsername(user.getId().toString())
                                .password(user.getPasswordHash())
                                .authorities(authorities)
                                .accountExpired(false)
                                .accountLocked(false)
                                .credentialsExpired(false)
                                .disabled(!user.isActive())
                                .build()
                        ))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found for ID: " + userId)));
    }
}