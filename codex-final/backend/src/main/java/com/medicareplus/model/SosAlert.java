package com.medicareplus.model;

import java.time.Instant;
import java.util.List;

public record SosAlert(
    String id,
    String patient,
    double latitude,
    double longitude,
    String status,
    List<String> notifiedContacts,
    String dutyDoctor,
    Instant createdAt
) {}
