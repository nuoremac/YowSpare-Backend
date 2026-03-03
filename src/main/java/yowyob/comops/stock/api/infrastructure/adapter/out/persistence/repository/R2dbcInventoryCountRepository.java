package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.InventoryCountEntity;

import java.util.UUID;

@Repository
public interface R2dbcInventoryCountRepository extends ReactiveCrudRepository<InventoryCountEntity, UUID> {
    Flux<InventoryCountEntity> findBySessionId(UUID sessionId);

    @Modifying
    @Query("DELETE FROM inventory_counts WHERE session_id = :sessionId")
    Mono<Void> deleteBySessionId(UUID sessionId);
}