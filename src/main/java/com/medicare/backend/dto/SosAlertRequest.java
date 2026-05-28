package com.medicare.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SosAlertRequest(
        @NotBlank String patientName,
        @NotBlank String ageGroup,
        @NotBlank String symptom,
        @NotBlank String latitude,
        @NotBlank String longitude,
        @NotBlank String note
) {
}
