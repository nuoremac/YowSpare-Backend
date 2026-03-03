package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockMovementItemEntity;

import java.util.UUID;

@Repository
public interface R2dbcStockMovementItemRepository extends ReactiveCrudRepository<StockMovementItemEntity, UUID> {
    Flux<StockMovementItemEntity> findByStockMovementId(UUID stockMovementId);
}