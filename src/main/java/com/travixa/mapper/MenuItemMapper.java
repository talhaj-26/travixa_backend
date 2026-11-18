package com.travixa.mapper;

import com.travixa.dto.restaurant.MenuItemRequest;
import com.travixa.dto.restaurant.MenuItemResponse;
import com.travixa.entity.restaurant.MenuCategory;
import com.travixa.entity.restaurant.MenuItem;

import java.util.Objects;

public final class MenuItemMapper {

    private MenuItemMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static MenuItem toEntity(MenuItemRequest request, MenuCategory category) {
        MenuItem item = new MenuItem();
        item.setCategory(Objects.requireNonNull(category, "category must not be null"));
        updateEntity(item, request);
        return item;
    }

    public static void updateEntity(MenuItem item, MenuItemRequest request) {
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setCurrency(request.currency());
        item.setStatus(request.status());
        item.setVegetarian(request.vegetarian());
        item.setDietaryInformation(request.dietaryInformation());
        item.setAllergenInformation(request.allergenInformation());
    }

    public static MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(item.getId(), item.getCategory().getId(), item.getName(), item.getDescription(),
                item.getPrice(), item.getCurrency(), item.getStatus(), item.isVegetarian(),
                item.getImageStorageKey() == null ? null : "/api/media/" + item.getImageStorageKey(),
                item.getDietaryInformation(), item.getAllergenInformation());
    }
}
