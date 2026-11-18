package com.travixa.repository.customer;

import com.travixa.entity.customer_engagement.SavedTrip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedTripRepository extends JpaRepository<SavedTrip, Long> {
}
