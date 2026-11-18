package com.travixa.dto.engagement;

import com.travixa.enums.NotificationType;
import jakarta.validation.constraints.NotNull;

public record NotificationPreferenceRequest(
        @NotNull NotificationType type,
        boolean enabled) {
}
