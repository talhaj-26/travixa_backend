package com.travixa.mapper;

import com.travixa.dto.restaurant.RestaurantReservationRequest;
import com.travixa.dto.restaurant.RestaurantReservationResponse;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.restaurant.DiningTable;
import com.travixa.entity.restaurant.Restaurant;
import com.travixa.entity.restaurant.RestaurantReservation;

import java.util.Objects;

public final class RestaurantReservationMapper {

    private RestaurantReservationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static RestaurantReservation toEntity(RestaurantReservationRequest request, Restaurant restaurant,
            DiningTable table, User customer) {
        RestaurantReservation reservation = new RestaurantReservation();
        reservation.setRestaurant(Objects.requireNonNull(restaurant, "restaurant must not be null"));
        reservation.setTable(table);
        reservation.setCustomer(Objects.requireNonNull(customer, "customer must not be null"));
        reservation.setStartsAt(request.startsAt());
        reservation.setGuestCount(request.guestCount());
        reservation.setSpecialRequests(request.specialRequests());
        return reservation;
    }

    public static RestaurantReservationResponse toResponse(RestaurantReservation reservation) {
        return new RestaurantReservationResponse(reservation.getId(), reservation.getRestaurant().getId(),
                reservation.getTable() == null ? null : reservation.getTable().getId(), reservation.getStartsAt(),
                reservation.getGuestCount(), reservation.getStatus(), reservation.getSpecialRequests());
    }
}
