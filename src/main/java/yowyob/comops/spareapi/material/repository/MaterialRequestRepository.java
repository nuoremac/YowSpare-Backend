package yowyob.comops.spareapi.material.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.material.entity.MaterialRequestEntity;

import java.util.UUID;

public interface MaterialRequestRepository extends ReactiveCrudRepository<MaterialRequestEntity, UUID> {

    @Query("""
            SELECT * FROM material_requests
            WHERE tenant_id = :tenantId
              AND id = :id
            LIMIT 1
            """)
    Mono<MaterialRequestEntity> findByTenantAndId(UUID tenantId, UUID id);

    @Query("""
            SELECT * FROM material_requests
            WHERE tenant_id = :tenantId
              AND (:agencyId IS NULL OR agency_id = :agencyId)
              AND (:departmentId IS NULL OR department_id = :departmentId)
              AND (:status IS NULL OR status = :status)
            ORDER BY updated_at DESC
            """)
    Flux<MaterialRequestEntity> search(UUID tenantId, UUID agencyId, UUID departmentId, String status);
}
