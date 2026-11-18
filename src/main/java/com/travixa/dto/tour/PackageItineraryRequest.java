package com.travixa.dto.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PackageItineraryRequest(
        @NotNull @Positive Long tourPackageId,
        @Positive int dayNumber,
        @NotBlank @Size(max = 160) String title,
        @Size(max = 4000) String description,
        @Size(max = 160) String locationName) {
}
