package com.medicare.backend.dto;

public record ProfileDto(
        long id,
        String name,
        String role,
        String tone
) {
}
