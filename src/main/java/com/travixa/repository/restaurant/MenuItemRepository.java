package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByCategory_Restaurant_IdOrderByNameAsc(Long restaurantId);
    Optional<MenuItem> findByImageStorageKey(String imageStorageKey);
}
