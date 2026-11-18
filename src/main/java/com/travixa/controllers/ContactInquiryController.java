package com.travixa.controllers;

import com.travixa.dto.support_platform.ContactInquiryRequest;
import com.travixa.dto.support_platform.ContactInquiryResponse;
import com.travixa.entity.platform_cms_support.ContactInquiry;
import com.travixa.services.platform.ContactInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contact-inquiries")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class ContactInquiryController {

    private final ContactInquiryService contactInquiryService;

    public ContactInquiryController(ContactInquiryService contactInquiryService) {
        this.contactInquiryService = contactInquiryService;
    }

    @GetMapping
    public List<ContactInquiryResponse> findAll() {
        return contactInquiryService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ContactInquiryResponse findById(@PathVariable Long id) {
        return contactInquiryService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact inquiry not found"));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ContactInquiryResponse> create(@Valid @RequestBody ContactInquiryRequest request) {
        ContactInquiry inquiry = new ContactInquiry();
        inquiry.setName(request.name());
        inquiry.setEmail(request.email());
        inquiry.setPhoneNumber(request.phoneNumber());
        inquiry.setSubject(request.subject());
        inquiry.setMessage(request.message());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(contactInquiryService.save(inquiry)));
    }

    private ContactInquiryResponse toResponse(ContactInquiry inquiry) {
        return new ContactInquiryResponse(inquiry.getId(), inquiry.getName(), inquiry.getEmail(),
                inquiry.getPhoneNumber(), inquiry.getSubject(), inquiry.getMessage(), inquiry.getStatus(),
                inquiry.getCreatedAt(), inquiry.getRespondedAt());
    }
}
