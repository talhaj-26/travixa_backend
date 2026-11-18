package com.travixa.dto.hotel;

public record HotelAmenityResponse(
        Long id,
        Long hotelId,
        String name,
        String description,
        boolean included
) {
}
