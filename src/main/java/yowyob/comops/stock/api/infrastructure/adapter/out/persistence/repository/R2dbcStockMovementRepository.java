package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockMovementEntity;

import java.util.UUID;

@Repository
public interface R2dbcStockMovementRepository extends ReactiveCrudRepository<StockMovementEntity, UUID> {
    
    Flux<StockMovementEntity> findByOrganizationId(UUID organizationId);

    @Query("""
        SELECT * FROM stock_movements 
        WHERE organization_id = :organizationId 
        AND (source_agency_id = :agencyId OR destination_agency_id = :agencyId)
    """)
    Flux<StockMovementEntity> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);
}