
package com.travixa.controllers;

import com.travixa.dto.hotel.RoomResponse;
import com.travixa.services.hotel.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponse> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    @GetMapping("/{id}")
    public RoomResponse getAvailableRoomById(@PathVariable Long id) {
        return roomService.getAvailableRoomById(id);
    }
}
