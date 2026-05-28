package com.medicareplus.dto;

import jakarta.validation.constraints.NotBlank;

public record ConsultationRequest(@NotBlank String patient, @NotBlank String doctor) {}
