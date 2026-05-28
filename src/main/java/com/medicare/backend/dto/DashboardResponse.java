package com.medicare.backend.dto;

import java.util.List;

public record DashboardResponse(
        String appName,
        List<MetricDto> metrics,
        List<AppointmentDto> appointments,
        List<MedicineDto> medicines,
        List<PharmacyDto> pharmacies,
        List<ProfileDto> profiles,
        MedicalRecordDto medicalRecord
) {
}
