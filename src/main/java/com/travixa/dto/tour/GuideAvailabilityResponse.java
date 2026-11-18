package com.travixa.dto.tour;

import com.travixa.enums.GuideAvailabilityStatus;

import java.time.Instant;

public record GuideAvailabilityResponse(
        Long id,
        Long guideProfileId,
        Instant startsAt,
        Instant endsAt,
        GuideAvailabilityStatus status) {
}
