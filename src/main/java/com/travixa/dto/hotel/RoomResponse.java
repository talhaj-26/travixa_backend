package com.travixa.dto.hotel;

import com.travixa.enums.RoomStatus;

public record RoomResponse(Long id, Long hotelId, Long roomTypeId, String roomNumber, RoomStatus status,
        String floorLabel, String viewDescription, java.util.List<com.travixa.dto.media.MediaUploadResponse> images) {
}
