package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.port.out.StockLevelRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcStockLevelRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StockLevelPersistenceAdapter implements StockLevelRepositoryPort {
    private final R2dbcStockLevelRepository repository;

    @Override
    public Mono<Void> adjustStock(UUID productId, UUID agencyId, Integer quantityDelta, Instant lastUpdated) {
        return repository.adjustStock(productId, agencyId, quantityDelta, lastUpdated).then();
    }

    @Override
    public Mono<Boolean> hasPositiveStockForProduct(UUID productId) {
        return repository.existsByProductIdAndQuantityGreaterThanZero(productId);
    }
}