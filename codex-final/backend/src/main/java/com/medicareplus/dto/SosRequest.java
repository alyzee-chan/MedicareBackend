package com.medicareplus.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SosRequest(
    @NotBlank String patient,
    double latitude,
    double longitude,
    List<String> contacts
) {}
