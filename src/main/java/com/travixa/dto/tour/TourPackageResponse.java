package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;

import java.math.BigDecimal;

public record TourPackageResponse(
        Long id,
        Long organizationId,
        Long destinationId,
        String destinationName,
        String name,
        String slug,
        String description,
        String inclusions,
        String exclusions,
        String cancellationPolicy,
        Integer bookingDeadlineHours,
        int durationDays,
        int maximumGroupSize,
        BigDecimal basePrice,
        CurrencyCode currency,
        ListingStatus listingStatus) {
}
