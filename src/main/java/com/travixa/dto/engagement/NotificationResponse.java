package com.travixa.dto.engagement;

import com.travixa.enums.NotificationType;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        boolean read,
        String actionUrl) {
}
