package yowyob.comops.spareapi.supplier.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.supplier.entity.SupplierProductEntity;

import java.util.UUID;

public interface SupplierProductRepository extends ReactiveCrudRepository<SupplierProductEntity, UUID> {

    @Query("""
            SELECT * FROM supplier_products
            WHERE tenant_id = :tenantId AND supplier_id = :supplierId
            ORDER BY preferred DESC, updated_at DESC
            """)
    Flux<SupplierProductEntity> listForSupplier(UUID tenantId, UUID supplierId);

    @Query("""
            SELECT * FROM supplier_products
            WHERE tenant_id = :tenantId AND product_id = :productId
            ORDER BY preferred DESC, updated_at DESC
            """)
    Flux<SupplierProductEntity> listForProduct(UUID tenantId, UUID productId);

    @Query("""
            SELECT * FROM supplier_products
            WHERE tenant_id = :tenantId AND supplier_id = :supplierId AND product_id = :productId
            LIMIT 1
            """)
    Mono<SupplierProductEntity> findOne(UUID tenantId, UUID supplierId, UUID productId);

    @Query("""
            DELETE FROM supplier_products
            WHERE tenant_id = :tenantId AND supplier_id = :supplierId AND product_id = :productId
            """)
    Mono<Void> deleteOne(UUID tenantId, UUID supplierId, UUID productId);
}

