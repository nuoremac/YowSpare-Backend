package yowyob.comops.spareapi.supplier.dto;

import jakarta.validation.constraints.NotBlank;

public record UpsertSupplierRequest(
        @NotBlank String name,
        String status,
        String email,
        String phone,
        String address,
        String notes
) {
}

