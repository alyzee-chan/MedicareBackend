package com.medicare.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record FamilyProfileRequest(
        @NotBlank String name,
        @NotBlank String role,
        String age,
        String bloodGroup
) {}
