package com.travixa.dto.restaurant;

import com.travixa.enums.ListingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(
        @NotNull @Positive Long organizationId,
        @Positive Long destinationId,
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 180) String slug,
        @Size(max = 5000) String description,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Email @Size(max = 254) String contactEmail,
        @Size(max = 32) String contactPhone,
        @Size(max = 120) String cuisineType,
        @Size(max = 500) String openingHours,
        @Size(max = 2000) String cancellationPolicy,
        @NotNull ListingStatus listingStatus) {
}
