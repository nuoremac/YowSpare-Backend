package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductEntity;

import java.util.UUID;

@Repository
public interface R2dbcProductRepository extends ReactiveCrudRepository<ProductEntity, UUID> {
    
    Flux<ProductEntity> findByOrganizationId(UUID organizationId);
    
    // Jointure : On ne veut que les produits qui ont une ligne de stock (même 0) dans l'agence donnée
    @Query("""
        SELECT p.* 
        FROM products p
        INNER JOIN stock_levels sl ON p.id = sl.product_id
        WHERE p.organization_id = :organizationId 
          AND sl.agency_id = :agencyId
    """)
    Flux<ProductEntity> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);
    
    @Query("SELECT COUNT(*) > 0 FROM products WHERE sku = :sku AND organization_id = :organizationId")
    Mono<Boolean> existsBySkuAndOrganizationId(String sku, UUID organizationId);
}