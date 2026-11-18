package com.travixa.dto.booking;

import com.travixa.enums.BookingStatus;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.PaymentStatus;

import java.math.BigDecimal;

public record CheckoutResponse(
        Long bookingId,
        Long paymentId,
        BookingStatus bookingStatus,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        CurrencyCode currency) {
}
