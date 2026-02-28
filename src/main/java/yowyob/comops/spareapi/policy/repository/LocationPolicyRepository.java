package yowyob.comops.spareapi.policy.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.policy.entity.LocationPolicyEntity;

import java.util.UUID;

public interface LocationPolicyRepository extends ReactiveCrudRepository<LocationPolicyEntity, UUID> {

    @Query("""
            SELECT * FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND bin_code = :binCode
              AND product_id IS NULL
            LIMIT 1
            """)
    Mono<LocationPolicyEntity> findGeneric(UUID tenantId, UUID agencyId, String binCode);

    @Query("""
            SELECT * FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND bin_code = :binCode
              AND product_id = :productId
            LIMIT 1
            """)
    Mono<LocationPolicyEntity> findForProduct(UUID tenantId, UUID agencyId, String binCode, UUID productId);

    @Query("""
            SELECT * FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND bin_code = :binCode
            ORDER BY product_id NULLS FIRST
            """)
    Flux<LocationPolicyEntity> findAllForBin(UUID tenantId, UUID agencyId, String binCode);

    @Query("""
            SELECT * FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
            ORDER BY bin_code ASC, product_id NULLS FIRST
            """)
    Flux<LocationPolicyEntity> findAllForAgency(UUID tenantId, UUID agencyId);

    @Query("""
            SELECT * FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND product_id = :productId
            ORDER BY bin_code ASC
            """)
    Flux<LocationPolicyEntity> findAllForAgencyAndProduct(UUID tenantId, UUID agencyId, UUID productId);

    @Query("""
            DELETE FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND bin_code = :binCode
              AND product_id IS NULL
            """)
    Mono<Void> deleteGeneric(UUID tenantId, UUID agencyId, String binCode);

    @Query("""
            DELETE FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND bin_code = :binCode
              AND product_id = :productId
            """)
    Mono<Void> deleteForProduct(UUID tenantId, UUID agencyId, String binCode, UUID productId);

    interface MaxRopAgg {
        UUID getProductId();
        Integer getReorderPoint();
    }

    @Query("""
            SELECT product_id, MAX(reorder_point) AS reorder_point
            FROM location_policies
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND product_id IS NOT NULL
              AND reorder_point IS NOT NULL
            GROUP BY product_id
            """)
    Flux<MaxRopAgg> maxReorderPointByProduct(UUID tenantId, UUID agencyId);
}
