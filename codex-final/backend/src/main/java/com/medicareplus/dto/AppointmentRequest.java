package com.medicareplus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AppointmentRequest(
    @NotBlank String patient,
    @NotBlank String specialty,
    @NotBlank String city,
    @NotNull LocalDate date,
    @NotBlank String time,
    @NotBlank String reminder
) {}
