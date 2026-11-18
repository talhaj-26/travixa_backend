package com.travixa.dto.tour;

import com.travixa.enums.TourGuideStatus;

public record TourGuideResponse(
        Long id,
        String userName,
        Long organizationId,
        String biography,
        String licenseNumber,
        String languageCodes,
        TourGuideStatus status) {
}
