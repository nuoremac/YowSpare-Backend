package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.UserEntity;
import java.util.UUID;

@Repository
public interface R2dbcUserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
    // Retourne TOUS les utilisateurs ayant cet email
    Flux<UserEntity> findByEmail(String email);

    // Vérification d'unicité scopée à l'organisation
    @Query("SELECT COUNT(*) > 0 FROM users WHERE email = :email AND organization_id = :organizationId")
    Mono<Boolean> existsByEmailAndOrganizationId(String email, UUID organizationId);

    // Vérification pour le Register initial (User sans org)
    @Query("SELECT COUNT(*) > 0 FROM users WHERE email = :email AND organization_id IS NULL")
    Mono<Boolean> existsByEmailAndOrganizationIdIsNull(String email);

    Mono<UserEntity> findByBusinessActorId(UUID businessActorId);
}