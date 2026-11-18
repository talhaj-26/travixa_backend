package com.travixa.dto.engagement;

import com.travixa.enums.JournalVisibility;

import java.time.Instant;

public record TravelJournalResponse(
        Long id,
        Long savedTripId,
        String title,
        String content,
        JournalVisibility visibility,
        Instant recordedAt) {
}
