package yowyob.comops.spareapi.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateReservationRequest(
        @NotNull UUID agencyId,
        @NotNull UUID productId,
        @NotNull @Min(1) Integer quantity,
        String referenceType,
        String referenceId,
        String note,
        Instant expiresAt
) {
}

