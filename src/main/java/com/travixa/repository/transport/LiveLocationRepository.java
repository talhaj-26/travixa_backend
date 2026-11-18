package com.travixa.repository.transport;

import com.travixa.entity.transport.LiveLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveLocationRepository extends JpaRepository<LiveLocation, Long> {
}
