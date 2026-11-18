package com.travixa.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RestaurantImageRequest(
        @NotNull @Positive Long restaurantId,
        @NotBlank @Size(max = 500) String storageKey,
        @Size(max = 160) String altText,
        @PositiveOrZero int displayOrder,
        boolean coverImage
) {
}
