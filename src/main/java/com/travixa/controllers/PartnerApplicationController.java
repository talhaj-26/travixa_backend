package com.travixa.controllers;

import com.travixa.dto.partner.PartnerApplicationResponse;
import com.travixa.dto.partner.PartnerApplicationRequest;
import com.travixa.dto.partner.PartnerApplicationReviewRequest;
import com.travixa.entity.partner_management.PartnerApplication;
import com.travixa.mapper.PartnerApplicationMapper;
import com.travixa.services.partner.PartnerApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/partners-applications")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class PartnerApplicationController {

    private final PartnerApplicationService partnerApplicationService;

    public PartnerApplicationController(PartnerApplicationService partnerApplicationService) {
        this.partnerApplicationService = partnerApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public PartnerApplicationResponse submit(@Valid @RequestBody PartnerApplicationRequest request,
            Authentication authentication) {
        return toResponse(partnerApplicationService.submit(request, authentication.getName()));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public PartnerApplicationResponse review(@PathVariable Long id,
            @Valid @RequestBody PartnerApplicationReviewRequest request, Authentication authentication) {
        return toResponse(partnerApplicationService.review(id, request, authentication.getName()));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PartnerApplicationResponse> findAll() {
        return partnerApplicationService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PartnerApplicationResponse findById(@PathVariable Long id) {
        return partnerApplicationService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner application not found"));
    }

    private PartnerApplicationResponse toResponse(PartnerApplication application) {
        return PartnerApplicationMapper.toResponse(application);
    }
}
