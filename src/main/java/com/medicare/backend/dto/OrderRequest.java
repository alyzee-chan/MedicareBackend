package com.medicare.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
        @NotBlank String customerName,
        @NotBlank String medicineName,
        @Min(1) int quantity,
        @NotBlank String paymentMethod,
        @NotBlank String pharmacyName,
        @NotBlank String fulfillmentMode
) {
}
