package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    List<DiningTable> findAllByRestaurant_IdOrderByTableNumberAsc(Long restaurantId);
    boolean existsByRestaurant_IdAndTableNumberIgnoreCase(Long restaurantId, String tableNumber);
    boolean existsByRestaurant_IdAndTableNumberIgnoreCaseAndIdNot(Long restaurantId, String tableNumber, Long id);
}
