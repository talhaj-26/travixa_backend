package com.travixa.controllers;

import com.travixa.dto.partner.PartnerMembershipResponse;
import com.travixa.entity.partner_management.PartnerMembership;
import com.travixa.mapper.PartnerMembershipMapper;
import com.travixa.services.partner.PartnerTeamService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/partners-memberships")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class PartnerTeamController {

    private final PartnerTeamService partnerTeamService;

    public PartnerTeamController(PartnerTeamService partnerTeamService) {
        this.partnerTeamService = partnerTeamService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PartnerMembershipResponse> findAll() {
        return partnerTeamService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN', 'PARTNER', 'HOTEL_PARTNER', 'HOTEL_STAFF', 'RESTAURANT_PARTNER', 'RESTAURANT_STAFF', 'TRAVEL_AGENCY_PARTNER', 'TRAVEL_AGENCY_STAFF', 'TOUR_GUIDE', 'TRANSPORT_PROVIDER', 'DRIVER')")
    @Transactional(readOnly = true)
    public List<PartnerMembershipResponse> findMine(Authentication authentication) {
        return partnerTeamService.findActiveByUserName(authentication.getName()).stream()
                .map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PartnerMembershipResponse findById(@PathVariable Long id) {
        return partnerTeamService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner membership not found"));
    }

    private PartnerMembershipResponse toResponse(PartnerMembership membership) {
        return PartnerMembershipMapper.toResponse(membership);
    }
}
