package com.medicare.backend.dto;

public record UserDto(
        long id,
        String fullName,
        String email,
        String phone,
        String dateOfBirth,
        String bloodGroup,
        String role,
        String avatarUrl,
        String createdAt
) {}
