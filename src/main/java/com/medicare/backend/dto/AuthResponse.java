package com.medicare.backend.dto;

public record AuthResponse(
        String token,
        long userId,
        String fullName,
        String email,
        String phone,
        String role,
        String bloodGroup,
        String dateOfBirth
) {}
