package yowyob.comops.spareapi.material.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentMemberRequest(
        @NotBlank String userId
) {
}

