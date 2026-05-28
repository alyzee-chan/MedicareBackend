package com.medicareplus.model;

public record Doctor(
    String id,
    String name,
    String specialty,
    String city,
    String nextSlot,
    String status,
    boolean dutyDoctor
) {}
