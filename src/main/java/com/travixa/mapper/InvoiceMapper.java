package com.travixa.mapper;

import com.travixa.dto.payment.InvoiceResponse;
import com.travixa.entity.payment_financial.Invoice;

public final class InvoiceMapper {

    private InvoiceMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(invoice.getId(), invoice.getBooking().getId(), invoice.getInvoiceNumber(),
                invoice.getIssuedOn(), invoice.getSubtotal(), invoice.getTaxAmount(), invoice.getTotalAmount(),
                invoice.getCurrency(), invoice.getStatus());
    }
}
