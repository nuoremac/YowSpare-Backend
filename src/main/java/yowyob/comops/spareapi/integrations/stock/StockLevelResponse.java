package yowyob.comops.spareapi.integrations.stock;

import java.time.Instant;
import java.util.UUID;

public record StockLevelResponse(
        UUID id,
        UUID productId,
        String productName,
        String productSku,
        UUID agencyId,
        Integer quantity,
        Integer availableQuantity,
        Instant lastUpdated
) {
}

