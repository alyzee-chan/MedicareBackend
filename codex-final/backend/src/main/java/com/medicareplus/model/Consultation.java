package com.medicareplus.model;

import java.time.Instant;
import java.util.List;

public record Consultation(
    String id,
    String patient,
    String doctor,
    String status,
    String roomUrl,
    List<String> messages,
    List<String> documents,
    String report,
    Instant startedAt
) {}
