package com.travixa.repository.booking;

import com.travixa.entity.booking.BookingGuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingGuestRepository extends JpaRepository<BookingGuest, Long> {

    List<BookingGuest> findAllByBooking_Id(Long bookingId);
}
