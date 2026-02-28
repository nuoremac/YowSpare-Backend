package yowyob.comops.spareapi.material.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDepartmentRequest(
        @NotNull UUID agencyId,
        @NotBlank String code,
        @NotBlank String name,
        Boolean active
) {
}
