package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record TourPackageRequest(
        @NotNull @Positive Long organizationId,
        @NotNull @Positive Long destinationId,
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 180) String slug,
        @Size(max = 8000) String description,
        @Size(max = 4000) String inclusions,
        @Size(max = 4000) String exclusions,
        @Size(max = 4000) String cancellationPolicy,
        @PositiveOrZero Integer bookingDeadlineHours,
        @Positive int durationDays,
        @Positive int maximumGroupSize,
        @NotNull @Positive BigDecimal basePrice,
        @NotNull CurrencyCode currency,
        @NotNull ListingStatus listingStatus) {
}
