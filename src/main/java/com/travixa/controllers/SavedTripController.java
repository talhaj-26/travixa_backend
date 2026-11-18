package com.travixa.controllers;

import com.travixa.dto.engagement.SavedTripResponse;
import com.travixa.entity.customer_engagement.SavedTrip;
import com.travixa.services.customer.SavedTripService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/saved-trips")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class SavedTripController {

    private final SavedTripService savedTripService;

    public SavedTripController(SavedTripService savedTripService) {
        this.savedTripService = savedTripService;
    }

    @GetMapping
    public List<SavedTripResponse> findAll() {
        return savedTripService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SavedTripResponse findById(@PathVariable Long id) {
        return savedTripService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saved trip not found"));
    }

    private SavedTripResponse toResponse(SavedTrip trip) {
        return new SavedTripResponse(trip.getId(), trip.getTitle(), trip.getDestinationName(), trip.getStartDate(),
                trip.getEndDate(), trip.getStatus(), trip.getNotes());
    }
}
