package com.medicareplus.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(@NotBlank String message) {}
