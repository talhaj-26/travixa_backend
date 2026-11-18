package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, Long> {
    List<RestaurantImage> findAllByRestaurant_IdOrderByDisplayOrderAscIdAsc(Long restaurantId);
    Optional<RestaurantImage> findByStorageKey(String storageKey);
}
