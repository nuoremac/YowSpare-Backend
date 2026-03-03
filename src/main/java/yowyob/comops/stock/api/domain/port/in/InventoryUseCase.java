package yowyob.comops.stock.api.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.InventoryCount;
import yowyob.comops.stock.api.domain.model.InventorySession;

import java.util.List;
import java.util.UUID;

public interface InventoryUseCase {
    Mono<InventorySession> initiateSession(InventorySession session);
    
    Mono<InventorySession> getSessionById(UUID id);
    
    Flux<InventorySession> getSessionsByAgency(UUID agencyId);

    // Saisie des comptages
    Mono<Void> submitCounts(UUID sessionId, List<InventoryCount> counts);

    // Validation et Génération des ajustements
    Mono<InventorySession> validateSession(UUID sessionId, UUID validatorId);
}