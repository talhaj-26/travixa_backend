package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.RestaurantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RestaurantReservationRepository
        extends JpaRepository<RestaurantReservation, Long>, JpaSpecificationExecutor<RestaurantReservation> {
    List<RestaurantReservation> findAllByCustomer_UserNameOrderByStartsAtDesc(String username);
    List<RestaurantReservation> findAllByRestaurant_IdOrderByStartsAtDesc(Long restaurantId);
}
