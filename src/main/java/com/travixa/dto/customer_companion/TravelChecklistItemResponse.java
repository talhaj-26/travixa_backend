package com.travixa.dto.customer_companion;

import java.time.Instant;

public record TravelChecklistItemResponse(
        Long id,
        Long savedTripId,
        String title,
        String category,
        Instant dueAt,
        Instant completedAt) {
}
