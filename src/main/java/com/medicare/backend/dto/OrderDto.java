package com.medicare.backend.dto;

public record OrderDto(
        long id,
        String orderNumber,
        String customerName,
        String medicineName,
        int quantity,
        String status,
        String paymentMethod,
        String eta,
        String pharmacyName,
        String fulfillmentMode
) {
}
