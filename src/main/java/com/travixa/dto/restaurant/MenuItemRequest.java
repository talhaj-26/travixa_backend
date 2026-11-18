package com.travixa.dto.restaurant;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.MenuItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemRequest(
        @NotNull @Positive Long categoryId,
        @NotBlank @Size(max = 140) String name,
        @Size(max = 2000) String description,
        @NotNull @Positive BigDecimal price,
        @NotNull CurrencyCode currency,
        @NotNull MenuItemStatus status,
        boolean vegetarian,
        @Size(max = 500) String dietaryInformation,
        @Size(max = 500) String allergenInformation) {
}
