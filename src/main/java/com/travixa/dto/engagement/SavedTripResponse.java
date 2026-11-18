package com.travixa.dto.engagement;

import com.travixa.enums.SavedTripStatus;

import java.time.LocalDate;

public record SavedTripResponse(
        Long id,
        String title,
        String destinationName,
        LocalDate startDate,
        LocalDate endDate,
        SavedTripStatus status,
        String notes) {
}
