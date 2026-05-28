package com.medicareplus.model;

public record Pharmacy(
    String id,
    String name,
    String city,
    double latitude,
    double longitude,
    String deliveryEta
) {}
