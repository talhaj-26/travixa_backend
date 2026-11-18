package com.travixa.dto.payment;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(
        Long id,
        Long bookingId,
        String invoiceNumber,
        LocalDate issuedOn,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        CurrencyCode currency,
        InvoiceStatus status) {
}
