package com.travixa.controllers;

import com.travixa.dto.restaurant.RestaurantReservationResponse;
import com.travixa.dto.restaurant.*;
import com.travixa.entity.restaurant.DiningTable;
import com.travixa.entity.restaurant.MenuCategory;
import com.travixa.entity.restaurant.MenuItem;
import com.travixa.entity.restaurant.RestaurantImage;
import com.travixa.entity.restaurant.RestaurantReservation;
import com.travixa.mapper.MenuItemMapper;
import com.travixa.mapper.RestaurantReservationMapper;
import com.travixa.services.restaurant.RestaurantOperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant-reservations")
public class RestaurantOperationController {

    private final RestaurantOperationService restaurantOperationService;

    public RestaurantOperationController(RestaurantOperationService restaurantOperationService) {
        this.restaurantOperationService = restaurantOperationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<RestaurantReservationResponse> findAll() {
        return restaurantOperationService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public RestaurantReservationResponse findById(@PathVariable Long id) {
        return restaurantOperationService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    private RestaurantReservationResponse toResponse(RestaurantReservation reservation) {
        return RestaurantReservationMapper.toResponse(reservation);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantReservationResponse create(
            @Valid @RequestBody RestaurantReservationRequest request,
            Authentication authentication
    ) {
        return toResponse(restaurantOperationService.createReservation(
                request,
                authentication.getName()
        ));
    }

    @GetMapping("/my")
    public List<RestaurantReservationResponse> findMine(Authentication authentication) {
        return restaurantOperationService.findForCustomer(authentication.getName())
                .stream().map(this::toResponse).toList();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public RestaurantReservationResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantReservationStatusUpdateRequest request
    ) {
        return toResponse(restaurantOperationService.updateReservationStatus(
                id,
                request.status()
        ));
    }
}
