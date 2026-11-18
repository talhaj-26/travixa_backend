package com.travixa.services.hotel;

import com.travixa.dto.hotel.RoomResponse;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.enums.RoomStatus;
import com.travixa.repository.hotel.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final com.travixa.services.platform.MediaService mediaService;

    public RoomService(RoomRepository roomRepository, com.travixa.services.platform.MediaService mediaService) {
        this.roomRepository = roomRepository;
        this.mediaService = mediaService;
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(this::isPubliclyAvailable)
                .map(room -> toResponse(room))
                .toList();
    }

    public RoomResponse getAvailableRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .filter(this::isPubliclyAvailable)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Available room not found"));

        return toResponse(room);
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getHotel().getId(),
                room.getRoomType().getId(),
                room.getRoomNumber(),
                room.getStatus(),
                room.getFloorLabel(),
                room.getViewDescription(), mediaService.list("ROOM", room.getId())
        );
    }

    private boolean isPubliclyAvailable(Room room) {
        return room.getStatus() == RoomStatus.AVAILABLE
                && room.getHotel().getListingStatus() == com.travixa.enums.ListingStatus.PUBLISHED
                && room.getRoomType().getListingStatus() == com.travixa.enums.ListingStatus.PUBLISHED;
    }
}
