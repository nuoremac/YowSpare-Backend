package yowyob.comops.stock.api.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.StockMovement;

import java.util.UUID;

public interface StockMovementRepositoryPort {
    Mono<StockMovement> save(StockMovement movement);
    Mono<StockMovement> findById(UUID id);
    
    // Filtres
    Flux<StockMovement> findByOrganizationId(UUID organizationId);
    
    // Trouve les mouvements impliquant cette agence (Source OU Destination)
    Flux<StockMovement> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);
}