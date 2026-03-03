package yowyob.comops.stock.api.infrastructure.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Ce service est un "no-op" (ne fait rien).
 * Sa seule existence en tant que Bean suffit à satisfaire l'auto-configuration de Spring Security
 * et à désactiver complètement la chaîne d'authentification par défaut (y compris la redirection vers /login).
 * Notre logique d'authentification passe par le AuthenticationManager personnalisé et non par ce service.
 */
@Service
public class NoOpUserDetailsService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Cette méthode ne devrait JAMAIS être appelée car notre AuthenticationManager ne l'utilise pas.
        // Si elle l'est, c'est une erreur de configuration.
        return Mono.error(new UsernameNotFoundException("NoOpUserDetailsService should not be called. Check security configuration."));
    }
}