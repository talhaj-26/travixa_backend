package com.travixa.dto.support_platform;

import com.travixa.enums.ChatConversationStatus;

import java.time.Instant;

public record ChatConversationResponse(
        Long id,
        String customerId,
        String assignedToId,
        String subject,
        ChatConversationStatus status,
        Instant startedAt,
        Instant lastMessageAt) {
}
