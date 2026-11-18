package com.travixa.dto.transport;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.TransportSeatStatus;

import java.math.BigDecimal;

public record TransportTicketResponse(
        Long id,
        Long scheduleId,
        String seatNumber,
        TransportSeatStatus status,
        BigDecimal price,
        CurrencyCode currency) {
}
