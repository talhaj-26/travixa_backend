package com.travixa.controllers;

import com.travixa.dto.tour.CustomTripResponse;
import com.travixa.dto.tour.CustomTripRequestDto;
import com.travixa.entity.tour_guide.CustomTripRequest;
import com.travixa.services.tour.CustomTripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/custom-trips")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class CustomTripController {

    private final CustomTripService customTripService;

    public CustomTripController(CustomTripService customTripService) {
        this.customTripService = customTripService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CustomTripResponse> findAll() {
        return customTripService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<CustomTripResponse> findMine(Authentication authentication) {
        return customTripService.findMine(authentication.getName()).stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<CustomTripResponse> create(@Valid @RequestBody CustomTripRequestDto request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(customTripService.create(request, authentication.getName())));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public CustomTripResponse findById(@PathVariable Long id) {
        return customTripService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Custom trip request not found"));
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public CustomTripResponse findMineById(@PathVariable Long id, Authentication authentication) {
        return customTripService.findMineById(id, authentication.getName()).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Custom trip request not found"));
    }

    private CustomTripResponse toResponse(CustomTripRequest request) {
        return new CustomTripResponse(request.getId(),
                request.getDestination() == null ? null : request.getDestination().getId(),
                request.getTravelerCount(), request.getStartDate(), request.getEndDate(), request.getTargetBudget(),
                request.getCurrency(), request.getRequirements(), request.getStatus());
    }
}
