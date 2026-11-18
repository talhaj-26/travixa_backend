package com.travixa.dto.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RoomTypeAvailabilityResponse(
        Long hotelId,
        Long roomTypeId,
        LocalDate checkIn,
        LocalDate checkOut,
        int requestedRooms,
        int availableUnits,
        int nights,
        boolean available,
        BigDecimal nightlyRate,
        BigDecimal totalRoomCost,
        String currency,
        List<DailyRate> dailyRates,
        String message
) {
    public record DailyRate(
            LocalDate date,
            BigDecimal nightlyRate,
            String currency
    ) {
    }
}