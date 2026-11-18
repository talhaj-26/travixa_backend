package com.travixa.dto.support_platform;

import com.travixa.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SupportTicketCreateRequest(
        @Positive Long organizationId,
        @NotBlank @Size(max = 200) String subject,
        @NotBlank @Size(max = 100) String category,
        @NotNull TicketPriority priority,
        @NotBlank @Size(max = 10000) String initialMessage) {
}
