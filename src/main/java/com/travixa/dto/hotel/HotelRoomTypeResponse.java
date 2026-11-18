
package com.travixa.dto.hotel;

import com.travixa.enums.BedType;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.RatePlanType;
import com.travixa.enums.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record HotelRoomTypeResponse(
        Long id,
        String name,
        RoomType category,
        BedType bedType,
        int maxOccupancy,
        BigDecimal sizeSquareMeters,
        Boolean airConditioned,
        String description,
        List<String> amenities,
        List<RoomTypeImageResponse> images,
        BigDecimal nightlyRate,
        CurrencyCode currency,
        RatePlanType ratePlanType
) {
}
