package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findAllByRestaurant_IdOrderByDisplayOrderAscNameAsc(Long restaurantId);
    boolean existsByRestaurant_IdAndNameIgnoreCase(Long restaurantId, String name);
    boolean existsByRestaurant_IdAndNameIgnoreCaseAndIdNot(Long restaurantId, String name, Long id);
}
