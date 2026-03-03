package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockLevelEntity;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface R2dbcStockLevelRepository extends ReactiveCrudRepository<StockLevelEntity, UUID> {

    Flux<StockLevelEntity> findByOrganizationId(UUID organizationId);

    Flux<StockLevelEntity> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);

    Mono<StockLevelEntity> findByProductIdAndAgencyId(UUID productId, UUID agencyId);

    @Modifying
    @Query("""
                INSERT INTO stock_levels (product_id, agency_id, organization_id, quantity, last_updated)
                VALUES (:productId, :agencyId, (SELECT organization_id FROM products WHERE id = :productId), :quantityDelta, :now)
                ON CONFLICT (product_id, agency_id) DO UPDATE
                SET quantity = stock_levels.quantity + :quantityDelta, last_updated = :now
            """)
    Mono<Integer> adjustStock(UUID productId, UUID agencyId, Integer quantityDelta, Instant now);

    @Query("SELECT COUNT(*) > 0 FROM stock_levels WHERE product_id = :productId AND quantity > 0")
    Mono<Boolean> existsByProductIdAndQuantityGreaterThanZero(UUID productId);
}