package com.travixa.dto.restaurant;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.MenuItemStatus;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        Long categoryId,
        String name,
        String description,
        BigDecimal price,
        CurrencyCode currency,
        MenuItemStatus status,
        boolean vegetarian,
        String imageUrl,
        String dietaryInformation,
        String allergenInformation) {
}
