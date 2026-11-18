package com.travixa.dto.restaurant;

public record MenuCategoryResponse(
        Long id,
        Long restaurantId,
        String name,
        String description,
        int displayOrder,
        boolean active
) {
}
