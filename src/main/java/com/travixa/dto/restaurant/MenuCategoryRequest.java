package com.travixa.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MenuCategoryRequest(
        @NotNull @Positive Long restaurantId,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 2000) String description,
        @PositiveOrZero int displayOrder,
        boolean active) {
}
