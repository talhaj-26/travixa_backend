package com.travixa.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HotelAmenityRequest(
        @NotNull @Positive Long hotelId,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 300) String description,
        boolean included) {
}
