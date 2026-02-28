package yowyob.comops.spareapi.supplier.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.supplier.entity.SupplierEntity;

import java.util.UUID;

public interface SupplierRepository extends ReactiveCrudRepository<SupplierEntity, UUID> {
    @Query("""
            SELECT * FROM suppliers
            WHERE tenant_id = :tenantId
              AND (:q IS NULL OR (name ILIKE '%' || :q || '%' OR email ILIKE '%' || :q || '%' OR phone ILIKE '%' || :q || '%'))
              AND (:status IS NULL OR status = :status)
            ORDER BY updated_at DESC
            """)
    Flux<SupplierEntity> search(UUID tenantId, String q, String status);

    @Query("""
            SELECT * FROM suppliers
            WHERE tenant_id = :tenantId AND id = :id
            LIMIT 1
            """)
    Mono<SupplierEntity> findByTenantAndId(UUID tenantId, UUID id);
}

