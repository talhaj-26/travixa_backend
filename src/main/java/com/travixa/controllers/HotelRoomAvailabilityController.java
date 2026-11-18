package com.travixa.controllers;

import com.travixa.dto.hotel.RoomTypeAvailabilityResponse;
import com.travixa.services.hotel.HotelRoomAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/hotels")
public class HotelRoomAvailabilityController {

    private final HotelRoomAvailabilityService availabilityService;

    public HotelRoomAvailabilityController(
            HotelRoomAvailabilityService availabilityService
    ) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{hotelId}/room-types/{roomTypeId}/availability")
    public RoomTypeAvailabilityResponse checkAvailability(
            @PathVariable Long hotelId,
            @PathVariable Long roomTypeId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut,
            @RequestParam(defaultValue = "1") int rooms
    ) {
        return availabilityService.checkAvailability(
                hotelId,
                roomTypeId,
                checkIn,
                checkOut,
                rooms
        );
    }
}