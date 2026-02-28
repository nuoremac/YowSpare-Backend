package yowyob.comops.spareapi.material.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.material.entity.MaterialRequestItemEntity;

import java.util.UUID;

public interface MaterialRequestItemRepository extends ReactiveCrudRepository<MaterialRequestItemEntity, UUID> {

    @Query("""
            SELECT * FROM material_request_items
            WHERE tenant_id = :tenantId
              AND request_id = :requestId
            ORDER BY created_at
            """)
    Flux<MaterialRequestItemEntity> findByTenantAndRequestId(UUID tenantId, UUID requestId);

    @Query("""
            SELECT * FROM material_request_items
            WHERE tenant_id = :tenantId
              AND request_id = :requestId
              AND product_id = :productId
            LIMIT 1
            """)
    Mono<MaterialRequestItemEntity> findByTenantRequestAndProduct(UUID tenantId, UUID requestId, UUID productId);
}
