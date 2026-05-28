package com.medicareplus.model;

import java.time.Instant;

public record Prescription(
    String id,
    String patient,
    String doctor,
    String drug,
    String dosage,
    String pharmacy,
    String signature,
    String qrCode,
    Instant createdAt
) {}
