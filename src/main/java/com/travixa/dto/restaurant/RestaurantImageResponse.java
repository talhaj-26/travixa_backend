package com.travixa.dto.restaurant;

public record RestaurantImageResponse(
        Long id,
        Long restaurantId,
        String storageKey,
        String publicUrl,
        String altText,
        int displayOrder,
        boolean coverImage
) {
}
