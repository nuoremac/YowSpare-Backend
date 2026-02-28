package yowyob.comops.spareapi.analytics.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.analytics.entity.AnalyticsRecommendationEntity;

import java.util.UUID;

public interface AnalyticsRecommendationRepository extends ReactiveCrudRepository<AnalyticsRecommendationEntity, UUID> {

    @Query("""
            SELECT * FROM analytics_recommendations
            WHERE tenant_id = :tenantId AND agency_id = :agencyId
            ORDER BY suggested_reorder_qty DESC NULLS LAST, updated_at DESC
            """)
    Flux<AnalyticsRecommendationEntity> list(UUID tenantId, UUID agencyId);

    @Query("""
            SELECT * FROM analytics_recommendations
            WHERE tenant_id = :tenantId AND agency_id = :agencyId AND product_id = :productId
            LIMIT 1
            """)
    Mono<AnalyticsRecommendationEntity> findOne(UUID tenantId, UUID agencyId, UUID productId);
}

