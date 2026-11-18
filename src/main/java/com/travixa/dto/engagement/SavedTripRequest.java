package com.travixa.dto.engagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SavedTripRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 150) String destinationName,
        LocalDate startDate,
        LocalDate endDate,
        @Size(max = 2000) String notes) {
}
