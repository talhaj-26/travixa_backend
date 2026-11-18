package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, Long> {
    List<RoomTypeImage> findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(Long roomTypeId);
    Optional<RoomTypeImage> findByStorageKey(String storageKey);
}
