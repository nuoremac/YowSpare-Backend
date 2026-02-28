package yowyob.comops.spareapi.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ProductLocationDto(
        UUID agencyId,
        UUID productId,
        @NotBlank String binCode,
        String note,
        Instant updatedAt
) {
    public static ProductLocationDto of(UUID agencyId, UUID productId, String binCode, String note, Instant updatedAt) {
        return new ProductLocationDto(agencyId, productId, binCode, note, updatedAt);
    }
}

