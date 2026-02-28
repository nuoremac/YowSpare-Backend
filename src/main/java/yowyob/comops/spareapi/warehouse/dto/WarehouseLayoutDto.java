package yowyob.comops.spareapi.warehouse.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WarehouseLayoutDto(
        UUID agencyId,
        @NotBlank String type,
        @NotNull @Min(1) Integer width,
        @NotNull @Min(1) Integer height,
        @NotNull JsonNode layout
) {
}

