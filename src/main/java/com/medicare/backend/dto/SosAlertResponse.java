package com.medicare.backend.dto;

import java.util.List;

public record SosAlertResponse(
        String status,
        String message,
        List<PharmacyDto> notifiedPharmacies
) {
}
