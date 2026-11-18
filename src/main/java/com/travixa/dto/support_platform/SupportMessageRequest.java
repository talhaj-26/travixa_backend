package com.travixa.dto.support_platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SupportMessageRequest(
        @NotNull @Positive Long ticketId,
        @NotBlank @Size(max = 10000) String message) {
}
