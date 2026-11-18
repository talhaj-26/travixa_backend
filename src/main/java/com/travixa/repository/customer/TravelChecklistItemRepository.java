package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.TravelChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelChecklistItemRepository extends JpaRepository<TravelChecklistItem, Long> {
}
