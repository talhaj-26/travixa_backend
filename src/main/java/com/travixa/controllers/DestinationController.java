package com.travixa.controllers;

import com.travixa.dto.hotel.DestinationRequest;
import com.travixa.dto.hotel.DestinationResponse;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.mapper.DestinationMapper;
import com.travixa.services.hotel.DestinationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    // Public: published destinations only
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<DestinationResponse> findPublished() {
        return destinationService.findPublished()
                .stream()
                .map(DestinationMapper::toResponse)
                .toList();
    }

    // Public: published destination detail
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public DestinationResponse findPublishedById(@PathVariable Long id) {
        return DestinationMapper.toResponse(
                destinationService.findPublishedById(id));
    }

    // Admin catalog: includes drafts and unpublished records
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public List<DestinationResponse> findAllForAdmin() {
        return destinationService.findAllForAdmin()
                .stream()
                .map(DestinationMapper::toResponse)
                .toList();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public DestinationResponse findByIdForAdmin(@PathVariable Long id) {
        return DestinationMapper.toResponse(
                destinationService.findByIdForAdmin(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public DestinationResponse create(
            @Valid @RequestBody DestinationRequest request) {
        return DestinationMapper.toResponse(destinationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public DestinationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request) {
        return DestinationMapper.toResponse(destinationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void delete(@PathVariable Long id) {
        destinationService.delete(id);
    }
}