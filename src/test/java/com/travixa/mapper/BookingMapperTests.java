package com.travixa.mapper;

import com.travixa.entity.booking.Booking;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.BookingStatus;
import com.travixa.enums.CurrencyCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTests {

    @Test
    void mapsCustomerContactFieldsForAuthenticatedBookingViews() {
        User customer = new User();
        customer.setUserName("traveler-7");
        customer.setUserFirstName("  Amina ");
        customer.setUserLastName(" Rahman  ");
        customer.setEmail("amina@example.com");

        Booking booking = bookingFor(customer);

        var response = BookingMapper.toResponse(booking, "PENDING", List.of(), List.of(), Map.of());

        assertEquals("Amina Rahman", response.customerName());
        assertEquals("amina@example.com", response.customerEmail());
    }

    @Test
    void fallsBackToAccountNameWhenCustomerHasNoProfileName() {
        User customer = new User();
        customer.setUserName("traveler-8");
        customer.setEmail("traveler@example.com");

        var response = BookingMapper.toResponse(bookingFor(customer), "PENDING", List.of(), List.of(), Map.of());

        assertEquals("traveler-8", response.customerName());
    }

    private Booking bookingFor(User customer) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setReferenceNumber("TRV-TEST-1");
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setSubtotalAmount(BigDecimal.ZERO);
        booking.setDiscountAmount(BigDecimal.ZERO);
        booking.setTotalAmount(BigDecimal.ZERO);
        booking.setCurrency(CurrencyCode.BDT);
        booking.setPlacedAt(Instant.parse("2026-10-30T00:00:00Z"));
        return booking;
    }
}
