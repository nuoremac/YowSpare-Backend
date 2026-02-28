package yowyob.comops.spareapi.material.dto;

import java.time.Instant;
import java.util.UUID;

public record DepartmentDto(
        UUID id,
        UUID agencyId,
        String code,
        String name,
        Boolean active,
        Instant updatedAt
) {
}
