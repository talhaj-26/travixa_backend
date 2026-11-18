package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.HotelAmenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelAmenityRepository extends JpaRepository<HotelAmenity, Long> {
    List<HotelAmenity> findAllByHotel_IdOrderByNameAscIdAsc(Long hotelId);
    Optional<HotelAmenity> findByIdAndHotel_Id(Long id, Long hotelId);
    boolean existsByHotel_IdAndNameIgnoreCase(Long hotelId, String name);
    boolean existsByHotel_IdAndNameIgnoreCaseAndIdNot(Long hotelId, String name, Long id);
}
