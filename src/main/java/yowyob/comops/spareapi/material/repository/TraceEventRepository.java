package yowyob.comops.spareapi.material.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import yowyob.comops.spareapi.material.entity.TraceEventEntity;

import java.util.UUID;

public interface TraceEventRepository extends ReactiveCrudRepository<TraceEventEntity, UUID> {

    @Query("""
            SELECT * FROM trace_events
            WHERE tenant_id = :tenantId
              AND entity_type = :entityType
              AND entity_id = :entityId
            ORDER BY created_at DESC
            """)
    Flux<TraceEventEntity> findForEntity(UUID tenantId, String entityType, UUID entityId);
}
