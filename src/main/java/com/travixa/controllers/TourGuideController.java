package com.travixa.controllers;

import com.travixa.dto.tour.TourGuideResponse;
import com.travixa.dto.tour.TourGuideProfileRequest;
import com.travixa.entity.tour_guide.TourGuideProfile;
import com.travixa.mapper.TourGuideMapper;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
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
@RequestMapping("/api/tours-guides")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TourGuideController {

    private final TourGuideService tourGuideService;
    private final UserRepository users;
    private final PartnerOrganizationRepository organizations;

    public TourGuideController(TourGuideService tourGuideService, UserRepository users,
                               PartnerOrganizationRepository organizations) {
        this.tourGuideService = tourGuideService;
        this.users = users;
        this.organizations = organizations;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<TourGuideResponse> findAll() {
        return tourGuideService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TourGuideResponse findById(@PathVariable Long id) {
        return tourGuideService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour guide not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public TourGuideResponse create(@Valid @RequestBody TourGuideProfileRequest request) {
        var user = users.findByUserName(request.userName().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account not found"));
        var organization = organizations.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found"));
        return toResponse(tourGuideService.save(TourGuideMapper.toEntity(request, user, organization)));
    }

    @PutMapping("/{id}")
    @Transactional
    public TourGuideResponse update(@PathVariable Long id, @Valid @RequestBody TourGuideProfileRequest request) {
        TourGuideProfile profile = tourGuideService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour guide not found"));
        var user = users.findByUserName(request.userName().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account not found"));
        var organization = organizations.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found"));
        profile.setUser(user); profile.setOrganization(organization); TourGuideMapper.updateEntity(profile, request);
        return toResponse(tourGuideService.save(profile));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable Long id) {
        TourGuideProfile profile = tourGuideService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour guide not found"));
        profile.setStatus(com.travixa.enums.TourGuideStatus.INACTIVE);
        tourGuideService.save(profile);
    }

    private TourGuideResponse toResponse(TourGuideProfile profile) {
        return TourGuideMapper.toResponse(profile);
    }
}
