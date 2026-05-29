package com.medicare.backend.dto;

public record FamilyProfileDto(
        long id,
        long userId,
        String name,
        String role,
        String age,
        String bloodGroup,
        String avatarUrl
) {}
