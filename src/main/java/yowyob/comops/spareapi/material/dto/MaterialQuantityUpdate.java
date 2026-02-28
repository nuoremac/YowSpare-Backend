package yowyob.comops.spareapi.material.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MaterialQuantityUpdate(
        @NotNull UUID productId,
        @NotNull @Min(1) Integer quantity,
        String note
) {
}
