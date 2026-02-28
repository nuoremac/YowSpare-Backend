package yowyob.comops.spareapi.warehouse.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.warehouse.entity.ProductLocationEntity;

import java.util.UUID;

public interface ProductLocationRepository extends ReactiveCrudRepository<ProductLocationEntity, UUID> {
    @Query("select * from product_locations where tenant_id = :tenantId and agency_id = :agencyId order by updated_at desc")
    Flux<ProductLocationEntity> findAllByTenantIdAndAgencyId(UUID tenantId, UUID agencyId);

    @Query("select * from product_locations where tenant_id = :tenantId and product_id = :productId order by updated_at desc")
    Flux<ProductLocationEntity> findAllByTenantIdAndProductId(UUID tenantId, UUID productId);

    @Query("select * from product_locations where tenant_id = :tenantId and agency_id = :agencyId and product_id = :productId limit 1")
    Mono<ProductLocationEntity> findOne(UUID tenantId, UUID agencyId, UUID productId);
}

