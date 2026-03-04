package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OrganizationEntity;
import java.util.UUID;

@Repository
public interface R2dbcOrganizationRepository extends ReactiveCrudRepository<OrganizationEntity, UUID> {
    // Pour trouver les orgs d'un propriétaire (fallback par défaut)
    Flux<OrganizationEntity> findByBusinessActorId(UUID businessActorId);

    // Vérification rapide de propriété
    @Query("SELECT COUNT(*) > 0 FROM organizations WHERE id = :organizationId AND business_actor_id = :businessActorId")
    Mono<Boolean> existsByIdAndBusinessActorId(UUID organizationId, UUID businessActorId);
}
