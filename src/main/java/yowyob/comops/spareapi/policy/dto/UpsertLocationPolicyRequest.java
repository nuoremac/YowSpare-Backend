package yowyob.comops.spareapi.policy.dto;

import jakarta.validation.constraints.Min;

public record UpsertLocationPolicyRequest(
        @Min(0) Integer minQty,
        @Min(0) Integer maxQty,
        @Min(0) Integer safetyStock,
        @Min(0) Integer reorderPoint,
        @Min(1) Integer cycleCountDays
) {
}

