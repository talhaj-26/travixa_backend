package com.travixa.dto.restaurant;

import com.travixa.enums.ListingStatus;

public record RestaurantResponse(
        Long id,
        Long organizationId,
        Long destinationId,
        String name,
        String slug,
        String description,
        String address,
        String contactEmail,
        String contactPhone,
        String cuisineType,
        String openingHours,
        String cancellationPolicy,
        ListingStatus listingStatus) {
}
