package yowyob.comops.spareapi.supplier.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SupplierProductDto(
        UUID id,
        UUID supplierId,
        UUID productId,
        Integer leadTimeDays,
        Integer moq,
        Boolean preferred,
        BigDecimal unitPrice,
        Instant updatedAt
) {
}

