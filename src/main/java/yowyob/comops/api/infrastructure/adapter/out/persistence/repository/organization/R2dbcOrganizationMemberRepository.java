package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OrganizationMemberEntity;
import java.util.UUID;

@Repository
public interface R2dbcOrganizationMemberRepository extends ReactiveCrudRepository<OrganizationMemberEntity, UUID> {
    // Récupérer tous les membres d'une organisation
    Flux<OrganizationMemberEntity> findByOrganizationId(UUID organizationId);

    // Récupérer toutes les adhésions d'un utilisateur (pour le login/scope)
    Flux<OrganizationMemberEntity> findByUserId(UUID userId);

    // Récupérer un membre spécifique dans une organisation (Contexte courant)
    Mono<OrganizationMemberEntity> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    // Récupérer les membres d'une agence spécifique (Filtrage RH)
    Flux<OrganizationMemberEntity> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);

    // Vérification rapide d'existence (Optimisé avec COUNT)
    @Query("SELECT COUNT(*) > 0 FROM organization_members WHERE organization_id = :organizationId AND user_id = :userId AND is_active = true")
    Mono<Boolean> existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);
}