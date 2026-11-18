package com.travixa.controllers;

import com.travixa.dto.support_platform.SupportTicketResponse;
import com.travixa.entity.platform_cms_support.SupportTicket;
import com.travixa.mapper.SupportTicketMapper;
import com.travixa.services.platform.SupportService;
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
@RequestMapping("/api/support-tickets")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> findAll() {
        return supportService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public SupportTicketResponse findById(@PathVariable Long id) {
        return supportService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Support ticket not found"));
    }

    private SupportTicketResponse toResponse(SupportTicket ticket) {
        return SupportTicketMapper.toResponse(ticket);
    }
}
