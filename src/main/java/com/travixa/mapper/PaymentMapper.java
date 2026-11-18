package com.travixa.mapper;

import com.travixa.dto.payment.PaymentResponse;
import com.travixa.entity.payment_financial.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getBooking().getId(), payment.getReferenceNumber(),
                payment.getAmount(), payment.getCurrency(), payment.getMethod(), payment.getStatus(),
                payment.getPaidAt());
    }
}
