package com.travixa.dto.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.time.LocalDate;

public record BookingCreateRequest(
        @NotBlank @Size(max = 100) String idempotencyKey,
        @NotEmpty List<@Valid BookingItemRequest> items,
        List<@Valid BookingGuestRequest> guests,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,
        @jakarta.validation.constraints.Size(max = 2000) String specialRequest,
        @jakarta.validation.constraints.Positive @jakarta.validation.constraints.Max(100) Integer guestCount,
        @jakarta.validation.constraints.Size(max = 40) String couponCode,
        @jakarta.validation.constraints.Size(max = 2000) String personalTouch) {
    public BookingCreateRequest(String idempotencyKey, List<BookingItemRequest> items,
                                List<BookingGuestRequest> guests) {
        this(idempotencyKey, items, guests, null, null, null, 1, null, null);
    }
    public BookingCreateRequest(String idempotencyKey, List<BookingItemRequest> items, List<BookingGuestRequest> guests,
                                LocalDate serviceStartDate, LocalDate serviceEndDate) {
        this(idempotencyKey, items, guests, serviceStartDate, serviceEndDate, null, 1, null, null);
    }
    public BookingCreateRequest(String idempotencyKey, List<BookingItemRequest> items, List<BookingGuestRequest> guests,
                                LocalDate serviceStartDate, LocalDate serviceEndDate, String specialRequest) {
        this(idempotencyKey, items, guests, serviceStartDate, serviceEndDate, specialRequest, 1, null, null);
    }
    public BookingCreateRequest(String idempotencyKey, List<BookingItemRequest> items, List<BookingGuestRequest> guests,
                                LocalDate serviceStartDate, LocalDate serviceEndDate, String specialRequest, Integer guestCount) {
        this(idempotencyKey, items, guests, serviceStartDate, serviceEndDate, specialRequest, guestCount, null, null);
    }
    public BookingCreateRequest(String idempotencyKey, List<BookingItemRequest> items, List<BookingGuestRequest> guests,
                                LocalDate serviceStartDate, LocalDate serviceEndDate, String specialRequest,
                                Integer guestCount, String couponCode) {
        this(idempotencyKey, items, guests, serviceStartDate, serviceEndDate, specialRequest, guestCount, couponCode, null);
    }
}
