package com.medicareplus.dto;

import jakarta.validation.constraints.NotBlank;

public record PrescriptionRequest(
    @NotBlank String patient,
    @NotBlank String doctor,
    @NotBlank String drug,
    @NotBlank String dosage,
    @NotBlank String pharmacy,
    @NotBlank String signature
) {}
