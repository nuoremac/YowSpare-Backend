package yowyob.comops.spareapi.material.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.material.entity.DepartmentMemberEntity;

import java.util.UUID;

public interface DepartmentMemberRepository extends ReactiveCrudRepository<DepartmentMemberEntity, UUID> {

    @Query("""
            SELECT * FROM department_members
            WHERE tenant_id = :tenantId
              AND department_id = :departmentId
            ORDER BY created_at DESC
            """)
    Flux<DepartmentMemberEntity> findByTenantAndDepartment(UUID tenantId, UUID departmentId);

    @Query("""
            SELECT * FROM department_members
            WHERE tenant_id = :tenantId
              AND department_id = :departmentId
              AND user_id = :userId
            LIMIT 1
            """)
    Mono<DepartmentMemberEntity> findByTenantDepartmentAndUserId(UUID tenantId, UUID departmentId, String userId);
}

