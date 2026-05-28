package com.medicareplus.model;

public record AiAdvice(
    String symptoms,
    String advice,
    String recommendedSpecialty,
    boolean urgent
) {}
