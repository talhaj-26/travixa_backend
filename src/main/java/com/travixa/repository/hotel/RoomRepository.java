package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.Room;
import com.travixa.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
    boolean existsByHotel_IdAndRoomNumberIgnoreCase(Long hotelId, String roomNumber);
    boolean existsByHotel_IdAndRoomNumberIgnoreCaseAndIdNot(Long hotelId, String roomNumber, Long id);
    List<Room> findAllByHotel_IdAndRoomType_IdAndStatus(
            Long hotelId,
            Long roomTypeId,
            RoomStatus status
    );
    List<Room> findAllByHotel_IdAndRoomType_IdAndStatusOrderByIdAsc(
            Long hotelId,
            Long roomTypeId,
            RoomStatus status
    );
}
