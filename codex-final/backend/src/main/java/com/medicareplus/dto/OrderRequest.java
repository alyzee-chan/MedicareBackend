package com.medicareplus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
    @NotBlank String drug,
    @NotBlank String pharmacyName,
    @Positive int price,
    @NotBlank String mobileMoneyPhone
) {}
