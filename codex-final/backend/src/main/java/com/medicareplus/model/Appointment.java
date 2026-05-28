package com.medicareplus.model;

import java.time.LocalDate;

public record Appointment(
    String id,
    String patient,
    String specialty,
    String city,
    LocalDate date,
    String time,
    String reminder,
    String status
) {}
