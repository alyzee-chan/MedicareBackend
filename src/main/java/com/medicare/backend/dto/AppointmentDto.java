package com.medicare.backend.dto;

public record AppointmentDto(
        long id,
        String patientName,
        String doctorName,
        String specialty,
        String clinic,
        String date,
        String time,
        String status,
        String reason
) {
}
