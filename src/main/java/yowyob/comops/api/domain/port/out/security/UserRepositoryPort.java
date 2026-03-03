package yowyob.comops.api.domain.port.out.security;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;
import java.util.UUID;

public interface UserRepositoryPort {
    // Pour l'authentification précise
    Mono<User> findById(UUID id);

    // Pour vérifier l'existence avant register (Global ou spécifique)
    Mono<User> findByEmail(String email);

    // Pour vérifier si un user "orphelin" existe déjà avec cet email
    Mono<Boolean> existsByEmailAndOrganizationIdIsNull(String email);

    Mono<User> save(User user);
}