package com.travixa.controllers;

import com.travixa.dto.restaurant.RestaurantResponse;
import com.travixa.dto.restaurant.RestaurantRequest;
import com.travixa.dto.restaurant.MenuCategoryResponse;
import com.travixa.dto.restaurant.MenuItemResponse;
import com.travixa.entity.restaurant.Restaurant;
import com.travixa.entity.restaurant.DiningTable;
import com.travixa.enums.RestaurantTableStatus;
import com.travixa.dto.restaurant.RestaurantTableResponse;
import com.travixa.dto.restaurant.RestaurantImageResponse;
import com.travixa.dto.restaurant.BuffetScheduleResponse;
import com.travixa.entity.restaurant.BuffetSchedule;
import com.travixa.mapper.RestaurantMapper;
import com.travixa.services.restaurant.RestaurantService;
import com.travixa.services.restaurant.RestaurantOperationService;
import com.travixa.mapper.MenuItemMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantOperationService restaurantOperationService;

    public RestaurantController(
            RestaurantService restaurantService,
            RestaurantOperationService restaurantOperationService
    ) {
        this.restaurantService = restaurantService;
        this.restaurantOperationService = restaurantOperationService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findPublished() {
        return restaurantService.findPublished().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RestaurantResponse findPublishedById(@PathVariable Long id) {
        return restaurantService.findPublishedById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published restaurant not found"
                ));
    }

    @GetMapping("/{id}/categories")
    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> findPublishedCategories(@PathVariable Long id) {
        return restaurantOperationService.findPublicCategories(id).stream()
                .map(category -> new MenuCategoryResponse(
                        category.getId(),
                        category.getRestaurant().getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getDisplayOrder(),
                        category.isActive()
                )).toList();
    }

    @GetMapping("/{id}/menu-items")
    @Transactional(readOnly = true)
    public List<MenuItemResponse> findPublishedMenuItems(@PathVariable Long id) {
        return restaurantOperationService.findPublicMenuItems(id).stream()
                .map(MenuItemMapper::toResponse).toList();
    }

    @GetMapping("/{id}/tables")
    @Transactional(readOnly = true)
    public List<RestaurantTableResponse> findPublishedTables(@PathVariable Long id) {
        return restaurantOperationService.findPublicTables(id).stream()
                .map(table -> new RestaurantTableResponse(
                        table.getId(), table.getRestaurant().getId(), table.getTableNumber(),
                        table.getCapacity(), table.getStatus()
                )).toList();
    }

    @GetMapping("/{id}/table-availability")
    @Transactional(readOnly = true)
    public List<com.travixa.dto.restaurant.TableAvailabilityResponse> findPublishedTableAvailability(
            @PathVariable Long id, @RequestParam java.time.LocalDate date) {
        return restaurantOperationService.findPublicTableAvailability(id, date).stream()
                .map(slot -> new com.travixa.dto.restaurant.TableAvailabilityResponse(
                        slot.getId(), slot.getTable().getRestaurant().getId(), slot.getTable().getId(),
                        slot.getTable().getTableNumber(), slot.getAvailableOn(), slot.getStartsAt(),
                        slot.getEndsAt(), slot.getAvailableSeats()))
                .toList();
    }

    @GetMapping("/{id}/buffet-schedules")
    @Transactional(readOnly = true)
    public List<BuffetScheduleResponse> findPublishedBuffetSchedules(@PathVariable Long id) {
        return restaurantOperationService.findPublicBuffetSchedules(id).stream()
                .map(this::toBuffetResponse).toList();
    }

    @GetMapping("/{id}/images")
    @Transactional(readOnly = true)
    public List<RestaurantImageResponse> findPublishedImages(@PathVariable Long id) {
        return restaurantOperationService.findPublicImages(id).stream()
                .map(image -> new RestaurantImageResponse(
                        image.getId(), image.getRestaurant().getId(), image.getStorageKey(),
                        "/api/media/" + image.getStorageKey(), image.getAltText(), image.getDisplayOrder(), image.isCoverImage()
                )).toList();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findAllForAdmin() {
        return restaurantService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public RestaurantResponse findByIdForAdmin(@PathVariable Long id) {
        return restaurantService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Restaurant not found"
                ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest request) {
        return toResponse(restaurantService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest request
    ) {
        return toResponse(restaurantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void delete(@PathVariable Long id) {
        restaurantService.delete(id);
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
        return RestaurantMapper.toResponse(restaurant);
    }

    private BuffetScheduleResponse toBuffetResponse(BuffetSchedule schedule) {
        return new BuffetScheduleResponse(schedule.getId(), schedule.getRestaurant().getId(),
                schedule.getBuffetDate(), schedule.getStartsAt(), schedule.getEndsAt(),
                schedule.getAdultPrice(), schedule.getChildPrice(), schedule.getCurrency(),
                schedule.getCapacity(), schedule.getMenuSummary(), schedule.getListingStatus());
    }
}
