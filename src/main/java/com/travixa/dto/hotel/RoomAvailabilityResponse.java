package com.travixa.dto.hotel;

import java.time.LocalDate;

public record RoomAvailabilityResponse(Long id, Long roomId, LocalDate availableOn, int availableUnits) {
}
