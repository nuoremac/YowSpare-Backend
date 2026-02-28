package yowyob.comops.spareapi.material.dto;

import java.util.UUID;

public record MaterialRequestItemDto(
        UUID productId,
        Integer quantityRequested,
        Integer quantityIssued,
        Integer quantityReturned,
        String note
) {
}
