package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelImageRepository extends JpaRepository<HotelImage, Long> {
    List<HotelImage> findAllByHotel_IdOrderByDisplayOrderAscIdAsc(Long hotelId);
    Optional<HotelImage> findByStorageKey(String storageKey);
}
