package com.medicare.backend.dto;

import java.util.List;

public record MedicalRecordDto(
        String patientName,
        List<String> allergies,
        List<String> treatments,
        List<String> vaccines,
        List<String> notes
) {
}
