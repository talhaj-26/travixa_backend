package com.travixa.controllers;

import com.travixa.dto.partner.PartnerApprovalResponse;
import com.travixa.entity.partner_management.PartnerApproval;
import com.travixa.services.partner.PartnerApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/partners-approvals")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class PartnerApprovalController {

    private final PartnerApprovalService partnerApprovalService;

    public PartnerApprovalController(PartnerApprovalService partnerApprovalService) {
        this.partnerApprovalService = partnerApprovalService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PartnerApprovalResponse> findAll() {
        return partnerApprovalService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PartnerApprovalResponse findById(@PathVariable Long id) {
        return partnerApprovalService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner approval not found"));
    }

    private PartnerApprovalResponse toResponse(PartnerApproval approval) {
        return new PartnerApprovalResponse(approval.getId(), approval.getApplication().getId(),
                approval.getReviewer().getUserName(), approval.getDecision(), approval.getNotes(),
                approval.getDecidedAt());
    }
}
