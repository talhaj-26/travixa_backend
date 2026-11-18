package com.travixa.repository.booking;

import com.travixa.entity.booking.BookingItem;
import com.travixa.enums.BookingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    List<BookingItem> findAllByBooking_Id(Long bookingId);

    boolean existsByResourceTypeAndResourceId(BookingType resourceType, Long resourceId);

    @Query("select coalesce(sum(i.quantity), 0) from BookingItem i " +
            "where i.resourceType = com.travixa.enums.BookingType.TRANSPORT " +
            "and i.resourceId = :scheduleId " +
            "and i.booking.status in (com.travixa.enums.BookingStatus.PENDING_PAYMENT, " +
            "com.travixa.enums.BookingStatus.CONFIRMED)")
    long countReservedTransportSeats(@Param("scheduleId") Long scheduleId);

    @Query("select coalesce(sum(i.quantity), 0) from BookingItem i " +
            "where i.resourceType = com.travixa.enums.BookingType.HOTEL " +
            "and i.resourceId = :roomId " +
            "and i.booking.status in (com.travixa.enums.BookingStatus.PENDING_PAYMENT, " +
            "com.travixa.enums.BookingStatus.CONFIRMED) " +
            "and i.booking.serviceStartDate is not null " +
            "and i.booking.serviceEndDate is not null " +
            "and i.booking.serviceStartDate < :checkOut " +
            "and i.booking.serviceEndDate > :checkIn")
    long countReservedRoomNights(@Param("roomId") Long roomId,
                                 @Param("checkIn") java.time.LocalDate checkIn,
                                 @Param("checkOut") java.time.LocalDate checkOut);
}
