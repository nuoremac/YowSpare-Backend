package yowyob.comops.spareapi.supplier.dto;

import java.time.Instant;
import java.util.UUID;

public record SupplierDto(
        UUID id,
        String name,
        String status,
        String email,
        String phone,
        String address,
        String notes,
        Instant updatedAt
) {
}

