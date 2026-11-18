package com.travixa.mapper;

import com.travixa.dto.hotel.RoomRequest;
import com.travixa.dto.hotel.RoomResponse;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomType;

import java.util.Objects;

public final class RoomMapper {

    private RoomMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Room toEntity(RoomRequest request, Hotel hotel, RoomType roomType) {
        Room room = new Room();
        room.setHotel(Objects.requireNonNull(hotel, "hotel must not be null"));
        room.setRoomType(Objects.requireNonNull(roomType, "roomType must not be null"));
        updateEntity(room, request);
        return room;
    }

    public static void updateEntity(Room room, RoomRequest request) {
        room.setRoomNumber(request.roomNumber());
        room.setStatus(request.status());
        room.setFloorLabel(request.floorLabel());
        room.setViewDescription(request.viewDescription());
    }

    public static RoomResponse toResponse(Room room) {
        return toResponse(room, java.util.List.of());
    }

    public static RoomResponse toResponse(Room room, java.util.List<com.travixa.dto.media.MediaUploadResponse> images) {
        return new RoomResponse(room.getId(), room.getHotel().getId(), room.getRoomType().getId(),
                room.getRoomNumber(), room.getStatus(), room.getFloorLabel(), room.getViewDescription(), images);
    }
}
