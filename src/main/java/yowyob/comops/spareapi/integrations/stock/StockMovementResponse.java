package yowyob.comops.spareapi.integrations.stock;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID organizationId,
        String reference,
        String type,
        String status,
        Instant date,
        UUID sourceAgencyId,
        UUID destinationAgencyId,
        String thirdPartyId,
        String notes,
        String createdBy,
        String validatedBy,
        Instant validatedAt,
        List<StockMovementItemResponse> items
) {
}

