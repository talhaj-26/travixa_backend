
package com.travixa.dto.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.time.LocalDate;

public record CartCheckoutRequest(
        @NotBlank @Size(max = 100) String idempotencyKey,
        List<@Valid BookingGuestRequest> guests,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,
        @jakarta.validation.constraints.Size(max = 2000) String specialRequest,
        @jakarta.validation.constraints.Positive @jakarta.validation.constraints.Max(100) Integer guestCount,
        @jakarta.validation.constraints.Size(max = 40) String couponCode,
        @jakarta.validation.constraints.Size(max = 2000) String personalTouch
) {
    public CartCheckoutRequest(String idempotencyKey, List<BookingGuestRequest> guests) {
        this(idempotencyKey, guests, null, null, null, 1, null, null);
    }

    public CartCheckoutRequest(String idempotencyKey, List<BookingGuestRequest> guests,
                               LocalDate serviceStartDate, LocalDate serviceEndDate) {
        this(idempotencyKey, guests, serviceStartDate, serviceEndDate, null, 1, null, null);
    }

    public CartCheckoutRequest(String idempotencyKey, List<BookingGuestRequest> guests,
                               LocalDate serviceStartDate, LocalDate serviceEndDate, String specialRequest) {
        this(idempotencyKey, guests, serviceStartDate, serviceEndDate, specialRequest, 1, null, null);
    }

    public CartCheckoutRequest(String idempotencyKey, List<BookingGuestRequest> guests,
                               LocalDate serviceStartDate, LocalDate serviceEndDate,
                               String specialRequest, Integer guestCount) {
        this(idempotencyKey, guests, serviceStartDate, serviceEndDate, specialRequest, guestCount, null, null);
    }
    public CartCheckoutRequest(String idempotencyKey, List<BookingGuestRequest> guests,
                               LocalDate serviceStartDate, LocalDate serviceEndDate, String specialRequest,
                               Integer guestCount, String couponCode) {
        this(idempotencyKey, guests, serviceStartDate, serviceEndDate, specialRequest, guestCount, couponCode, null);
    }
}
