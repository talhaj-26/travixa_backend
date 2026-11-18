package com.travixa.dto.transport;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.TransportSeatStatus;

import java.math.BigDecimal;

public record TransportSeatResponse(
        Long id,
        Long scheduleId,
        String seatNumber,
        TransportSeatStatus status,
        BigDecimal priceOverride,
        CurrencyCode currency) {
}
