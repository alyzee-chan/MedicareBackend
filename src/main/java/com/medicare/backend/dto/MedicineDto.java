package com.medicare.backend.dto;

public record MedicineDto(
        long id,
        String name,
        String form,
        String price,
        String stock,
        String alternative,
        String note
) {
}
