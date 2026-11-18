package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository
        extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findAllByRoom_IdInAndAvailableOnBetween(
            Collection<Long> roomIds,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<RoomAvailability> findById(Long id);
    boolean existsByRoom_IdAndAvailableOn(Long roomId, LocalDate availableOn);
    boolean existsByRoom_IdAndAvailableOnAndIdNot(Long roomId, LocalDate availableOn, Long id);
}
