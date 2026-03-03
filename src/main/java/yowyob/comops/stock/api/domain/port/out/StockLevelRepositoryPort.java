package yowyob.comops.stock.api.domain.port.out;

import reactor.core.publisher.Mono;
import java.time.Instant;
import java.util.UUID;

public interface StockLevelRepositoryPort {
    Mono<Void> adjustStock(UUID productId, UUID agencyId, Integer quantityDelta, Instant lastUpdated);
    Mono<Boolean> hasPositiveStockForProduct(UUID productId); // Nouvelle méthode
}