package yowyob.comops.spareapi.policy.dto;

import java.time.Instant;
import java.util.UUID;

public record LocationPolicyDto(
        UUID id,
        UUID agencyId,
        String binCode,
        UUID productId,
        Integer minQty,
        Integer maxQty,
        Integer safetyStock,
        Integer reorderPoint,
        Integer cycleCountDays,
        Instant updatedAt
) {
}

