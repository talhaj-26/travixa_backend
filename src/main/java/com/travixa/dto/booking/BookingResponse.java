package com.travixa.dto.booking;

import com.travixa.enums.BookingStatus;
import com.travixa.enums.BookingType;
import com.travixa.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record BookingResponse(
        Long id,
        String referenceNumber,
        BookingStatus status,
        String paymentStatus,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        String couponCode,
        BigDecimal totalAmount,
        CurrencyCode currency,
        Instant placedAt,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,
        String specialRequest,
        String personalTouch,
        int guestCount,
        String customerName,
        String customerEmail,
        List<Item> items,
        List<Guest> guests) {

    public record Item(BookingType resourceType, Long resourceId, String description, int quantity,
            BigDecimal unitPrice, BigDecimal lineTotal, CurrencyCode currency, HotelDetails hotelDetails) {
    }

    public record HotelDetails(Long hotelId, String hotelName, String destinationName, String destinationRegion,
            String destinationCountryCode, String address, int starRating, String hotelImageUrl,
            Long roomTypeId, String roomTypeName, String roomCategory, String roomNumber, String floorLabel,
            String viewDescription, String roomImageUrl, java.time.LocalTime checkInTime, java.time.LocalTime checkOutTime,
            String cancellationPolicy, String childPolicy) {
    }

    public record Guest(String fullName, String email, String phoneNumber, boolean leadGuest) {
    }
}
