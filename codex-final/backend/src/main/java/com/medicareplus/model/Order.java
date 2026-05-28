package com.medicareplus.model;

import java.time.Instant;

public record Order(
    String id,
    String drug,
    String pharmacyName,
    int price,
    String mobileMoneyPhone,
    String status,
    String tracking,
    Instant createdAt
) {}
