package com.travixa.dto.customer_companion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TravelChecklistItemRequest(
        @Positive Long savedTripId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 80) String category,
        Instant dueAt) {
}
