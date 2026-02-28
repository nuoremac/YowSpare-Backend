package yowyob.comops.spareapi.workflow.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.workflow.entity.WorkflowRequestEntity;

import java.util.UUID;

public interface WorkflowRequestRepository extends ReactiveCrudRepository<WorkflowRequestEntity, UUID> {

    @Query("""
            SELECT * FROM workflow_requests
            WHERE tenant_id = :tenantId
              AND (:status IS NULL OR status = :status)
              AND (:type IS NULL OR type = :type)
            ORDER BY updated_at DESC
            """)
    Flux<WorkflowRequestEntity> search(UUID tenantId, String status, String type);

    @Query("""
            SELECT * FROM workflow_requests
            WHERE tenant_id = :tenantId AND id = :id
            LIMIT 1
            """)
    Mono<WorkflowRequestEntity> findByTenantAndId(UUID tenantId, UUID id);
}

