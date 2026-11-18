package com.travixa.dto.restaurant;

import com.travixa.enums.RestaurantTableStatus;

public record RestaurantTableResponse(
        Long id,
        Long restaurantId,
        String tableNumber,
        int capacity,
        RestaurantTableStatus status
) {
}
