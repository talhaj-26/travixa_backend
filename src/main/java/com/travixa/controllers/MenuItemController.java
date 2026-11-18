
package com.travixa.controllers;

import com.travixa.dto.restaurant.MenuItemResponse;
import com.travixa.services.restaurant.MenuItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public List<MenuItemResponse> getAvailableMenuItems() {
        return menuItemService.getAvailableMenuItems();
    }

    @GetMapping("/{id}")
    public MenuItemResponse getAvailableMenuItemById(@PathVariable Long id) {
        return menuItemService.getAvailableMenuItemById(id);
    }
}
