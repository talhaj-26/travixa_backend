package com.travixa.controllers;

import com.travixa.dto.partner.PartnerOrganizationResponse;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.mapper.PartnerOrganizationMapper;
import com.travixa.services.partner.PartnerOrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.travixa.dto.partner.PartnerOrganizationRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/partners-organizations")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class PartnerOrganizationController {

    private final PartnerOrganizationService partnerOrganizationService;

    public PartnerOrganizationController(PartnerOrganizationService partnerOrganizationService) {
        this.partnerOrganizationService = partnerOrganizationService;
    }

    @GetMapping
    public List<PartnerOrganizationResponse> findAll() {
        return partnerOrganizationService.findAll().stream().map(this::toResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartnerOrganizationResponse create(
            @Valid @RequestBody PartnerOrganizationRequest request
    ) {
        return toResponse(partnerOrganizationService.create(request));
    }

    @PutMapping("/{id}")
    public PartnerOrganizationResponse update(@PathVariable Long id, @Valid @RequestBody PartnerOrganizationRequest request) {
        return toResponse(partnerOrganizationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { partnerOrganizationService.delete(id); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN') or "
            + "@partnerAccessEvaluator.canAccessOrganization(authentication.name, #p0)")
    public PartnerOrganizationResponse findById(@PathVariable Long id) {
        return partnerOrganizationService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner organization not found"));
    }

    private PartnerOrganizationResponse toResponse(PartnerOrganization organization) {
        return PartnerOrganizationMapper.toResponse(organization);
    }
}
