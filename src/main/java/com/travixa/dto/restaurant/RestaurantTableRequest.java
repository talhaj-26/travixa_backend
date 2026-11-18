package com.travixa.dto.restaurant;

import com.travixa.enums.RestaurantTableStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RestaurantTableRequest(
        @NotNull @Positive Long restaurantId,
        @NotBlank @Size(max = 40) String tableNumber,
        @Positive int capacity,
        @NotNull RestaurantTableStatus status) {
}
