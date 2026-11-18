package com.travixa.controllers;

import com.travixa.dto.transport.DriverProfileRequest;
import com.travixa.dto.transport.DriverProfileResponse;
import com.travixa.entity.transport.DriverProfile;
import com.travixa.enums.DriverStatus;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.transport.DriverProfileRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/driver-profiles")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class DriverProfileController {
    private final DriverProfileRepository profiles;
    private final UserRepository users;
    private final PartnerOrganizationRepository organizations;

    public DriverProfileController(DriverProfileRepository profiles, UserRepository users,
                                   PartnerOrganizationRepository organizations) {
        this.profiles = profiles;
        this.users = users;
        this.organizations = organizations;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<DriverProfileResponse> findAll() {
        return profiles.findAll().stream().map(this::response).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public DriverProfileResponse create(@Valid @RequestBody DriverProfileRequest request) {
        if (profiles.existsByUser_UserName(request.userName().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A driver profile already exists for this user");
        }
        return response(profiles.save(toEntity(new DriverProfile(), request)));
    }

    @PutMapping("/{id}")
    @Transactional
    public DriverProfileResponse update(@PathVariable Long id, @Valid @RequestBody DriverProfileRequest request) {
        DriverProfile profile = profiles.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver profile not found"));
        if (!profile.getUser().getUserName().equalsIgnoreCase(request.userName().trim())
                && profiles.existsByUser_UserName(request.userName().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A driver profile already exists for this user");
        }
        return response(profiles.save(toEntity(profile, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deactivate(@PathVariable Long id) {
        DriverProfile profile = profiles.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver profile not found"));
        profile.setStatus(DriverStatus.INACTIVE);
        profiles.save(profile);
    }

    private DriverProfile toEntity(DriverProfile profile, DriverProfileRequest request) {
        profile.setUser(users.findByUserName(request.userName().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account not found")));
        profile.setOrganization(organizations.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization not found")));
        profile.setLicenseReference(request.licenseReference() == null ? null : request.licenseReference().trim());
        profile.setStatus(request.status());
        return profile;
    }

    private DriverProfileResponse response(DriverProfile profile) {
        return new DriverProfileResponse(profile.getId(), profile.getUser().getUserName(),
                profile.getOrganization().getId(), profile.getOrganization().getName(),
                profile.getLicenseReference(), profile.getStatus());
    }
}
