package com.travixa.dto.support_platform;

import java.time.Instant;

public record ChatMessageResponse(
        Long id,
        Long conversationId,
        String senderId,
        String content,
        Instant sentAt,
        Instant readAt) {
}
