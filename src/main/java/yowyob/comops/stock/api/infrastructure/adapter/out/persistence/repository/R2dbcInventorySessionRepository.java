package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.InventorySessionEntity;

import java.util.UUID;

@Repository
public interface R2dbcInventorySessionRepository extends ReactiveCrudRepository<InventorySessionEntity, UUID> {
    Flux<InventorySessionEntity> findByAgencyId(UUID agencyId);
}