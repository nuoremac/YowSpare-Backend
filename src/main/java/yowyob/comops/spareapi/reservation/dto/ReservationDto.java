package yowyob.comops.spareapi.reservation.dto;

import java.time.Instant;
import java.util.UUID;

public record ReservationDto(
        UUID id,
        UUID agencyId,
        UUID productId,
        Integer quantity,
        String status,
        String referenceType,
        String referenceId,
        String note,
        String createdBy,
        Instant expiresAt,
        Instant updatedAt
) {
}

