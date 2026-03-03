package yowyob.comops.stock.api.domain.port.in;

import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.domain.model.StockLevel;

import java.util.UUID;

public interface StockLevelUseCase {
    Flux<StockLevel> getStockLevels(UUID organizationId);
}