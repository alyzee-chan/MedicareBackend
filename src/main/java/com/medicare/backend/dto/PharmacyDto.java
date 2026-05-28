package com.medicare.backend.dto;

public record PharmacyDto(
        long id,
        String name,
        double rating,
        String distance,
        String label,
        String open,
        String city
) {
}
