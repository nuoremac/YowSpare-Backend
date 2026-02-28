package yowyob.comops.spareapi.material.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.material.entity.DepartmentEntity;

import java.util.UUID;

public interface DepartmentRepository extends ReactiveCrudRepository<DepartmentEntity, UUID> {

    @Query("""
            SELECT * FROM departments
            WHERE tenant_id = :tenantId
              AND (:agencyId IS NULL OR agency_id = :agencyId)
              AND (:active IS NULL OR active = :active)
            ORDER BY name
            """)
    Flux<DepartmentEntity> search(UUID tenantId, UUID agencyId, Boolean active);

    @Query("""
            SELECT * FROM departments
            WHERE tenant_id = :tenantId
              AND id = :id
            LIMIT 1
            """)
    Mono<DepartmentEntity> findByTenantAndId(UUID tenantId, UUID id);

    @Query("""
            SELECT * FROM departments
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND code = :code
            LIMIT 1
            """)
    Mono<DepartmentEntity> findByTenantAgencyAndCode(UUID tenantId, UUID agencyId, String code);
}
