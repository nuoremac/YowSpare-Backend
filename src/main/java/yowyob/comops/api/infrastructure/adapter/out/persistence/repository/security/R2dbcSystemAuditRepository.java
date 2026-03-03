package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.SystemAuditEntity;
import java.util.UUID;

@Repository
public interface R2dbcSystemAuditRepository extends ReactiveCrudRepository<SystemAuditEntity, UUID> {
    @Query("SELECT * FROM system_audits WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    Flux<SystemAuditEntity> findByUserId(UUID userId, int limit);

    @Query("SELECT * FROM system_audits WHERE organization_id = :orgId ORDER BY created_at DESC LIMIT :limit")
    Flux<SystemAuditEntity> findByOrganizationId(UUID orgId, int limit);
}
