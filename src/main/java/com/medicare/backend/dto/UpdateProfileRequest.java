package com.medicare.backend.dto;

public record UpdateProfileRequest(
        String fullName,
        String phone,
        String dateOfBirth,
        String bloodGroup
) {}
