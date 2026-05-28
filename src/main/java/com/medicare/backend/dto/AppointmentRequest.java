package com.medicare.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AppointmentRequest(
        @NotBlank String patientName,
        @NotBlank String doctorName,
        @NotBlank String specialty,
        @NotBlank String clinic,
        @NotBlank String date,
        @NotBlank String time,
        String reason
) {
}
