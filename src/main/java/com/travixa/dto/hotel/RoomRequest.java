package com.travixa.dto.hotel;

import com.travixa.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RoomRequest(
        @NotNull @Positive Long hotelId,
        @NotNull @Positive Long roomTypeId,
        @NotBlank @Size(max = 40) String roomNumber,
        @NotNull RoomStatus status,
        @Size(max = 40) String floorLabel,
        @Size(max = 120) String viewDescription) {
}
