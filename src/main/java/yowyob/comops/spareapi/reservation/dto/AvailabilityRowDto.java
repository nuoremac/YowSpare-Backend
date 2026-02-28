package yowyob.comops.spareapi.reservation.dto;

import java.util.UUID;

public record AvailabilityRowDto(
        UUID agencyId,
        UUID productId,
        Integer onHand,
        Integer reserved,
        Integer available
) {
}

