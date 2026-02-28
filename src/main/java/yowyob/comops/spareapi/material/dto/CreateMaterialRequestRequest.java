package yowyob.comops.spareapi.material.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateMaterialRequestRequest(
        @NotNull UUID departmentId,
        String reasonCode,
        String reasonText,
        Instant expectedReturnAt,
        @NotNull @Size(min = 1) List<@Valid MaterialRequestItemInput> items
) {
}
