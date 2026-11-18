package com.travixa.services.restaurant;
import com.travixa.dto.restaurant.MenuItemResponse;
import com.travixa.entity.restaurant.MenuItem;
import com.travixa.enums.MenuItemStatus;
import com.travixa.enums.ListingStatus;
import com.travixa.repository.restaurant.MenuItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItemResponse> getAvailableMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .filter(item -> item.getStatus() == MenuItemStatus.AVAILABLE)
                .filter(item -> item.getCategory().isActive())
                .filter(item -> item.getCategory().getRestaurant().getListingStatus() == ListingStatus.PUBLISHED)
                .map(this::toResponse)
                .toList();
    }

    public MenuItemResponse getAvailableMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .filter(menuItem -> menuItem.getStatus() == MenuItemStatus.AVAILABLE)
                .filter(menuItem -> menuItem.getCategory().isActive())
                .filter(menuItem -> menuItem.getCategory().getRestaurant().getListingStatus() == ListingStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Available menu item not found"));

        return toResponse(item);
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getCategory().getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCurrency(),
                item.getStatus(),
                item.isVegetarian(),
                item.getImageStorageKey() == null ? null : "/api/media/" + item.getImageStorageKey(),
                item.getDietaryInformation(),
                item.getAllergenInformation()
        );
    }
}
