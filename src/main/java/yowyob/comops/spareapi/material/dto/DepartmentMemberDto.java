package yowyob.comops.spareapi.material.dto;

import java.time.Instant;
import java.util.UUID;

public record DepartmentMemberDto(
        UUID id,
        UUID departmentId,
        String userId,
        Instant updatedAt
) {
}

