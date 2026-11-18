package com.travixa.dto.support_platform;

import com.travixa.enums.SupportTicketStatus;
import com.travixa.enums.TicketPriority;

import java.time.Instant;

public record SupportTicketResponse(
        Long id,
        Long organizationId,
        String referenceNumber,
        String subject,
        String category,
        TicketPriority priority,
        SupportTicketStatus status,
        Instant resolvedAt) {
}
