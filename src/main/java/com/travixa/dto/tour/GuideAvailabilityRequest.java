package com.travixa.dto.tour;

import com.travixa.enums.GuideAvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record GuideAvailabilityRequest(
        @NotNull @Positive Long guideProfileId,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        @NotNull GuideAvailabilityStatus status) {
}
