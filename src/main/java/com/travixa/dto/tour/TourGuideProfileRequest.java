package com.travixa.dto.tour;

import com.travixa.enums.TourGuideStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TourGuideProfileRequest(
        @NotBlank @Size(max = 100) String userName,
        @NotNull @Positive Long organizationId,
        @Size(max = 3000) String biography,
        @Size(max = 80) String licenseNumber,
        @Size(max = 500) String languageCodes,
        @NotNull TourGuideStatus status) {
}
