package yowyob.comops.stock.api.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.InventorySession;

import java.util.UUID;

public interface InventoryRepositoryPort {
    Mono<InventorySession> save(InventorySession session);

    Mono<InventorySession> findById(UUID id);

    Flux<InventorySession> findByAgencyId(UUID agencyId);
}