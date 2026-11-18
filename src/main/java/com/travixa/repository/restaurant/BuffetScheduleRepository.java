package com.travixa.repository.restaurant;

import com.travixa.entity.restaurant.BuffetSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuffetScheduleRepository extends JpaRepository<BuffetSchedule, Long> {
    List<BuffetSchedule> findAllByRestaurant_IdOrderByBuffetDateAscStartsAtAsc(Long restaurantId);
}
