package com.travixa.controllers;

import com.travixa.dto.restaurant.*;
import com.travixa.entity.restaurant.DiningTable;
import com.travixa.entity.restaurant.MenuCategory;
import com.travixa.entity.restaurant.MenuItem;
import com.travixa.entity.restaurant.RestaurantImage;
import com.travixa.entity.restaurant.BuffetSchedule;
import com.travixa.mapper.MenuItemMapper;
import com.travixa.services.restaurant.RestaurantOperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant-catalog")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class RestaurantCatalogController {

    private final RestaurantOperationService operations;

    public RestaurantCatalogController(RestaurantOperationService operations) {
        this.operations = operations;
    }

    @GetMapping("/{restaurantId}/categories")
    public List<MenuCategoryResponse> categories(@PathVariable Long restaurantId) {
        return operations.findCategories(restaurantId).stream()
                .map(this::toCategoryResponse).toList();
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public MenuCategoryResponse createCategory(@Valid @RequestBody MenuCategoryRequest request) {
        return toCategoryResponse(operations.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public MenuCategoryResponse updateCategory(@PathVariable Long id,
                                                @Valid @RequestBody MenuCategoryRequest request) {
        return toCategoryResponse(operations.updateCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        operations.deleteCategory(id);
    }

    @GetMapping("/{restaurantId}/menu-items")
    public List<MenuItemResponse> menuItems(@PathVariable Long restaurantId) {
        return operations.findMenuItems(restaurantId).stream()
                .map(MenuItemMapper::toResponse).toList();
    }

    @PostMapping("/menu-items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public MenuItemResponse createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return MenuItemMapper.toResponse(operations.createMenuItem(request));
    }

    @PutMapping("/menu-items/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public MenuItemResponse updateMenuItem(@PathVariable Long id,
                                            @Valid @RequestBody MenuItemRequest request) {
        return MenuItemMapper.toResponse(operations.updateMenuItem(id, request));
    }

    @DeleteMapping("/menu-items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void deleteMenuItem(@PathVariable Long id) {
        operations.deleteMenuItem(id);
    }

    @GetMapping("/{restaurantId}/tables")
    public List<RestaurantTableResponse> tables(@PathVariable Long restaurantId) {
        return operations.findTables(restaurantId).stream()
                .map(this::toTableResponse).toList();
    }

    @GetMapping("/{restaurantId}/table-availability")
    public List<TableAvailabilityResponse> tableAvailability(@PathVariable Long restaurantId) {
        return operations.findTableAvailability(restaurantId).stream()
                .map(this::toTableAvailabilityResponse).toList();
    }

    @PostMapping("/table-availability")
    @ResponseStatus(HttpStatus.CREATED)
    public TableAvailabilityResponse createTableAvailability(@Valid @RequestBody TableAvailabilityRequest request) {
        return toTableAvailabilityResponse(operations.createTableAvailability(request));
    }

    @PutMapping("/table-availability/{id}")
    public TableAvailabilityResponse updateTableAvailability(@PathVariable Long id,
                                                              @Valid @RequestBody TableAvailabilityRequest request) {
        return toTableAvailabilityResponse(operations.updateTableAvailability(id, request));
    }

    @DeleteMapping("/table-availability/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTableAvailability(@PathVariable Long id) {
        operations.deleteTableAvailability(id);
    }

    @PostMapping("/tables")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantTableResponse createTable(@Valid @RequestBody RestaurantTableRequest request) {
        return toTableResponse(operations.createTable(request));
    }

    @PutMapping("/tables/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantTableResponse updateTable(@PathVariable Long id,
                                                @Valid @RequestBody RestaurantTableRequest request) {
        return toTableResponse(operations.updateTable(id, request));
    }

    @DeleteMapping("/tables/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void deleteTable(@PathVariable Long id) {
        operations.deleteTable(id);
    }

    @GetMapping("/{restaurantId}/images")
    public List<RestaurantImageResponse> images(@PathVariable Long restaurantId) {
        return operations.findImages(restaurantId).stream()
                .map(this::toImageResponse).toList();
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantImageResponse createImage(@Valid @RequestBody RestaurantImageRequest request) {
        return toImageResponse(operations.createImage(request));
    }

    @PutMapping("/images/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantImageResponse updateImage(@PathVariable Long id,
                                                @Valid @RequestBody RestaurantImageRequest request) {
        return toImageResponse(operations.updateImage(id, request));
    }

    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void deleteImage(@PathVariable Long id) {
        operations.deleteImage(id);
    }

    @GetMapping("/{restaurantId}/buffet-schedules")
    public List<BuffetScheduleResponse> buffetSchedules(@PathVariable Long restaurantId) {
        return operations.findBuffetSchedules(restaurantId).stream()
                .map(this::toBuffetResponse).toList();
    }

    @PostMapping("/buffet-schedules")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public BuffetScheduleResponse createBuffetSchedule(@Valid @RequestBody BuffetScheduleRequest request) {
        return toBuffetResponse(operations.createBuffetSchedule(request));
    }

    @PutMapping("/buffet-schedules/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public BuffetScheduleResponse updateBuffetSchedule(@PathVariable Long id,
                                                        @Valid @RequestBody BuffetScheduleRequest request) {
        return toBuffetResponse(operations.updateBuffetSchedule(id, request));
    }

    @DeleteMapping("/buffet-schedules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void deleteBuffetSchedule(@PathVariable Long id) {
        operations.deleteBuffetSchedule(id);
    }

    private MenuCategoryResponse toCategoryResponse(MenuCategory category) {
        return new MenuCategoryResponse(category.getId(), category.getRestaurant().getId(),
                category.getName(), category.getDescription(), category.getDisplayOrder(), category.isActive());
    }

    private RestaurantTableResponse toTableResponse(DiningTable table) {
        return new RestaurantTableResponse(table.getId(), table.getRestaurant().getId(),
                table.getTableNumber(), table.getCapacity(), table.getStatus());
    }

    private TableAvailabilityResponse toTableAvailabilityResponse(com.travixa.entity.restaurant.TableAvailability slot) {
        return new TableAvailabilityResponse(slot.getId(), slot.getTable().getRestaurant().getId(),
                slot.getTable().getId(), slot.getTable().getTableNumber(), slot.getAvailableOn(),
                slot.getStartsAt(), slot.getEndsAt(), slot.getAvailableSeats());
    }

    private RestaurantImageResponse toImageResponse(RestaurantImage image) {
        return new RestaurantImageResponse(image.getId(), image.getRestaurant().getId(),
                image.getStorageKey(), "/api/media/" + image.getStorageKey(), image.getAltText(), image.getDisplayOrder(), image.isCoverImage());
    }

    private BuffetScheduleResponse toBuffetResponse(BuffetSchedule schedule) {
        return new BuffetScheduleResponse(
                schedule.getId(), schedule.getRestaurant().getId(), schedule.getBuffetDate(),
                schedule.getStartsAt(), schedule.getEndsAt(), schedule.getAdultPrice(),
                schedule.getChildPrice(), schedule.getCurrency(), schedule.getCapacity(),
                schedule.getMenuSummary(), schedule.getListingStatus()
        );
    }
}
