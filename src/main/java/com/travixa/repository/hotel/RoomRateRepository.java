package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.RoomRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RoomRateRepository
        extends JpaRepository<RoomRate, Long> {

    Optional<RoomRate>
    findFirstByRoomType_IdAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqualOrderByValidFromDescNightlyRateAsc(
            Long roomTypeId,
            LocalDateTime validFrom,
            LocalDateTime validUntil
    );
}
