package com.medicareplus.model;

public record DrugStock(
    String id,
    String drug,
    String pharmacyId,
    String pharmacyName,
    String city,
    int quantity,
    int price
) {}
