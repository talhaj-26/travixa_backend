package com.travixa.dto.support_platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotNull @Positive Long conversationId,
        @NotBlank @Size(max = 10000) String content) {
}
