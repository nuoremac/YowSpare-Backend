package yowyob.comops.spareapi.reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateReservationStatusRequest(
        @NotBlank String status
) {
}

