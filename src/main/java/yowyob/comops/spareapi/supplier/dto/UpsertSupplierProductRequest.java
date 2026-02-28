package yowyob.comops.spareapi.supplier.dto;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record UpsertSupplierProductRequest(
        @Min(0) Integer leadTimeDays,
        @Min(0) Integer moq,
        Boolean preferred,
        BigDecimal unitPrice
) {
}

