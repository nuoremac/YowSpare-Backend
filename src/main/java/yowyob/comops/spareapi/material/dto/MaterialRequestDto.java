package yowyob.comops.spareapi.material.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MaterialRequestDto(
        UUID id,
        UUID agencyId,
        UUID departmentId,
        String status,
        String reasonCode,
        String reasonText,
        String requestedBy,
        String approvedBy,
        Instant approvedAt,
        String issuedBy,
        Instant issuedAt,
        Instant expectedReturnAt,
        String closedBy,
        Instant closedAt,
        String closeReason,
        Instant updatedAt,
        List<MaterialRequestItemDto> items
) {
}
