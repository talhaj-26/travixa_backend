
package com.travixa.controllers;

import com.travixa.dto.hotel.HotelResponse;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.enums.ListingStatus;
import com.travixa.mapper.HotelMapper;
import com.travixa.services.hotel.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.travixa.dto.hotel.HotelRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<HotelResponse> findPublished() {
        return hotelService.findPublished()
                .stream()
                .map(HotelMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public HotelResponse findPublishedById(@PathVariable Long id) {
        Hotel hotel = hotelService.findPublishedById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Published hotel not found"));

        return HotelMapper.toResponse(hotel);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<HotelResponse> findAllForAdmin() {
        return hotelService.findAll()
                .stream()
                .map(HotelMapper::toResponse)
                .toList();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public HotelResponse findByIdForAdmin(@PathVariable Long id) {
        Hotel hotel = hotelService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"));

        return HotelMapper.toResponse(hotel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public HotelResponse create(@Valid @RequestBody HotelRequest request) {
        return HotelMapper.toResponse(hotelService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public HotelResponse update(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request
    ) {
        return HotelMapper.toResponse(hotelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void delete(@PathVariable Long id) {
        hotelService.delete(id);
    }
}
