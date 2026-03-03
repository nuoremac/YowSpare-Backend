package yowyob.comops.stock.api.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.StockMovement;

import java.util.UUID;

public interface StockMovementUseCase {
    Mono<StockMovement> createDraft(StockMovement movement);

    Mono<StockMovement> validateMovement(UUID movementId, UUID validatorId);

    Flux<StockMovement> getAllMovements(UUID organizationId);

    Mono<StockMovement> getMovementById(UUID id);
}