package yowyob.comops.spareapi.warehouse.dto;

import jakarta.validation.constraints.NotBlank;

public record UpsertProductLocationRequest(
        @NotBlank String binCode,
        String note
) {
}

