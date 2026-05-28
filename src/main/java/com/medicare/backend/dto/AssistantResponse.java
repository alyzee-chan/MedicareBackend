package com.medicare.backend.dto;

import java.util.List;

public record AssistantResponse(
        String input,
        String recommendation,
        String urgencyLevel,
        List<String> advice
) {
}
