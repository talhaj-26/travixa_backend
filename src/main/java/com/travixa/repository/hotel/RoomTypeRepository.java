
package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.enums.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomTypeRepository
        extends JpaRepository<RoomType, Long> {

    List<RoomType> findAllByHotel_IdOrderByNameAsc(Long hotelId);

    List<RoomType> findAllByHotel_IdAndListingStatusOrderByNameAsc(
            Long hotelId,
            ListingStatus listingStatus
    );

    boolean existsByHotel_IdAndNameIgnoreCase(
            Long hotelId,
            String name
    );

    boolean existsByHotel_IdAndNameIgnoreCaseAndIdNot(
            Long hotelId,
            String name,
            Long id
    );
}
