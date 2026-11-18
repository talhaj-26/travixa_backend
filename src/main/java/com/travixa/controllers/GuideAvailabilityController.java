package com.travixa.controllers;

import com.travixa.dto.tour.GuideAvailabilityResponse;
import com.travixa.dto.tour.GuideAvailabilityRequest;
import com.travixa.entity.tour_guide.GuideAvailability;
import com.travixa.services.tour.GuideAvailabilityService;
import com.travixa.services.tour.TourGuideService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tours/guide-availability")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class GuideAvailabilityController {

    private final GuideAvailabilityService guideAvailabilityService;
    private final TourGuideService tourGuideService;

    public GuideAvailabilityController(GuideAvailabilityService guideAvailabilityService, TourGuideService tourGuideService) {
        this.guideAvailabilityService = guideAvailabilityService;
        this.tourGuideService = tourGuideService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<GuideAvailabilityResponse> findAll() {
        return guideAvailabilityService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public GuideAvailabilityResponse findById(@PathVariable Long id) {
        return guideAvailabilityService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide availability not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public GuideAvailabilityResponse create(@Valid @RequestBody GuideAvailabilityRequest request) {
        return toResponse(guideAvailabilityService.save(toEntity(new GuideAvailability(), request)));
    }

    @PutMapping("/{id}")
    @Transactional
    public GuideAvailabilityResponse update(@PathVariable Long id, @Valid @RequestBody GuideAvailabilityRequest request) {
        GuideAvailability availability = guideAvailabilityService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide availability not found"));
        return toResponse(guideAvailabilityService.save(toEntity(availability, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        GuideAvailability availability = guideAvailabilityService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide availability not found"));
        guideAvailabilityService.delete(availability);
    }

    private GuideAvailability toEntity(GuideAvailability item, GuideAvailabilityRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability end must be after start");
        }
        var guide = tourGuideService.findById(request.guideProfileId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide profile not found"));
        item.setGuideProfile(guide); item.setStartsAt(request.startsAt()); item.setEndsAt(request.endsAt()); item.setStatus(request.status());
        return item;
    }

    private GuideAvailabilityResponse toResponse(GuideAvailability availability) {
        return new GuideAvailabilityResponse(availability.getId(), availability.getGuideProfile().getId(),
                availability.getStartsAt(), availability.getEndsAt(), availability.getStatus());
    }
}
