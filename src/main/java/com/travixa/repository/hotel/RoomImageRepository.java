package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    List<RoomImage> findAllByRoom_IdOrderByDisplayOrderAscIdAsc(Long roomId);
    Optional<RoomImage> findByStorageKey(String storageKey);
}
