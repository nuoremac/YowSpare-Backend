package yowyob.comops.spareapi.integrations.stock;

import java.util.UUID;

public record StockMovementItemResponse(
        UUID id,
        UUID productId,
        String productName,
        String productCode,
        Integer quantity
) {
}

