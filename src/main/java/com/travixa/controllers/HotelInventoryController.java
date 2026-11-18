
package com.travixa.controllers;

import com.travixa.dto.hotel.RoomAvailabilityResponse;
import com.travixa.dto.hotel.RoomAvailabilityRequest;
import com.travixa.dto.hotel.RoomRateRequest;
import com.travixa.dto.hotel.RoomRateResponse;
import com.travixa.dto.hotel.RoomRequest;
import com.travixa.dto.hotel.RoomResponse;
import com.travixa.dto.hotel.RoomTypeRequest;
import com.travixa.dto.hotel.RoomTypeResponse;
import com.travixa.entity.hotel_stays.Room;
import com.travixa.entity.hotel_stays.RoomAvailability;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.mapper.RoomMapper;
import com.travixa.mapper.RoomTypeMapper;
import com.travixa.services.hotel.HotelInventoryService;
import com.travixa.services.platform.MediaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/hotel-inventory")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class HotelInventoryController {

    private final HotelInventoryService hotelInventoryService;
    private final MediaService mediaService;

    public HotelInventoryController(
            HotelInventoryService hotelInventoryService,
            MediaService mediaService
    ) {
        this.hotelInventoryService = hotelInventoryService;
        this.mediaService = mediaService;
    }

    @GetMapping("/rooms")
    @Transactional(readOnly = true)
    public List<RoomResponse> findRooms() {
        return hotelInventoryService.findAllRooms()
                .stream()
                .map(room -> RoomMapper.toResponse(room, mediaService.list("ROOM", room.getId())))
                .toList();
    }

    @GetMapping("/rooms/{id}")
    @Transactional(readOnly = true)
    public RoomResponse findRoomById(@PathVariable Long id) {
        Room room = hotelInventoryService.findRoomById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room not found"
                ));

        return RoomMapper.toResponse(room, mediaService.list("ROOM", room.getId()));
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) {
        Room room = hotelInventoryService.createRoom(request);
        return RoomMapper.toResponse(room, mediaService.list("ROOM", room.getId()));
    }

    @PutMapping("/rooms/{id}")
    public RoomResponse updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        Room room = hotelInventoryService.updateRoom(id, request);
        return RoomMapper.toResponse(room, mediaService.list("ROOM", room.getId()));
    }

    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable Long id) {
        hotelInventoryService.deleteRoom(id);
    }

    @GetMapping("/room-types")
    @Transactional(readOnly = true)
    public List<RoomTypeResponse> findRoomTypes() {
        return hotelInventoryService.findAllRoomTypes()
                .stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
    }

    @GetMapping("/room-types/{id}")
    @Transactional(readOnly = true)
    public RoomTypeResponse findRoomTypeById(@PathVariable Long id) {
        RoomType roomType = hotelInventoryService.findRoomTypeById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room type not found"
                ));

        return RoomTypeMapper.toResponse(roomType);
    }

    @PostMapping("/room-types")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RoomTypeResponse createRoomType(
            @Valid @RequestBody RoomTypeRequest request
    ) {
        return RoomTypeMapper.toResponse(
                hotelInventoryService.createRoomType(request)
        );
    }

    @PutMapping("/room-types/{id}")
    @Transactional
    public RoomTypeResponse updateRoomType(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequest request
    ) {
        return RoomTypeMapper.toResponse(
                hotelInventoryService.updateRoomType(id, request)
        );
    }

    @DeleteMapping("/room-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoomType(@PathVariable Long id) {
        hotelInventoryService.deleteRoomType(id);
    }

    @GetMapping("/rates")
    public List<RoomRateResponse> findRates() {
        return hotelInventoryService.findAllRates()
                .stream()
                .map(this::toRateResponse)
                .toList();
    }

    @PostMapping("/rates")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomRateResponse createRate(
            @Valid @RequestBody RoomRateRequest request
    ) {
        return toRateResponse(
                hotelInventoryService.createRate(request)
        );
    }

    @PutMapping("/rates/{id}")
    public RoomRateResponse updateRate(
            @PathVariable Long id,
            @Valid @RequestBody RoomRateRequest request
    ) {
        return toRateResponse(
                hotelInventoryService.updateRate(id, request)
        );
    }

    @DeleteMapping("/rates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRate(@PathVariable Long id) {
        hotelInventoryService.deleteRate(id);
    }

    @GetMapping("/availability")
    public List<RoomAvailabilityResponse> findAvailability() {
        return hotelInventoryService.findAllAvailability()
                .stream()
                .map(this::toAvailabilityResponse)
                .toList();
    }

    @PostMapping("/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomAvailabilityResponse createAvailability(@Valid @RequestBody RoomAvailabilityRequest request) {
        return toAvailabilityResponse(hotelInventoryService.createAvailability(request));
    }

    @PutMapping("/availability/{id}")
    public RoomAvailabilityResponse updateAvailability(@PathVariable Long id,
                                                       @Valid @RequestBody RoomAvailabilityRequest request) {
        return toAvailabilityResponse(hotelInventoryService.updateAvailability(id, request));
    }

    @DeleteMapping("/availability/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvailability(@PathVariable Long id) {
        hotelInventoryService.deleteAvailability(id);
    }

    private RoomRateResponse toRateResponse(RoomRate rate) {
        return new RoomRateResponse(
                rate.getId(),
                rate.getRoomType().getId(),
                rate.getRatePlanType(),
                rate.getNightlyRate(),
                rate.getCurrency(),
                rate.getValidFrom(),
                rate.getValidUntil(),
                rate.isActive()
        );
    }

    private RoomAvailabilityResponse toAvailabilityResponse(
            RoomAvailability availability
    ) {
        return new RoomAvailabilityResponse(
                availability.getId(),
                availability.getRoom().getId(),
                availability.getAvailableOn(),
                availability.getAvailableUnits()
        );
    }
}
