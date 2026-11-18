
package com.travixa.controllers;

import com.travixa.dto.payment.RefundCreateRequest;
import com.travixa.dto.payment.RefundResponse;
import com.travixa.dto.payment.RefundReviewRequest;
import com.travixa.entity.payment_financial.RefundRequest;
import com.travixa.enums.RefundStatus;
import com.travixa.mapper.RefundMapper;
import com.travixa.services.payment.RefundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse create(
            @Valid @RequestBody RefundCreateRequest request,
            Authentication authentication) {

        return RefundMapper.toResponse(
                refundService.create(request, authentication.getName()));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<RefundResponse> findAll(Authentication authentication) {

        List<RefundRequest> refunds = hasFinancialAuthority(authentication)
                ? refundService.findAll()
                : refundService.findAllByCustomer(authentication.getName());

        return refunds.stream()
                .map(RefundMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public RefundResponse findById(
            @PathVariable Long id,
            Authentication authentication) {

        return (hasFinancialAuthority(authentication)
                ? refundService.findById(id)
                : refundService.findByIdAndCustomer(
                id, authentication.getName()))
                .map(RefundMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Refund request not found"));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN', 'FINANCE_ACCOUNTANT')")
    public RefundResponse review(
            @PathVariable Long id,
            @Valid @RequestBody RefundReviewRequest request,
            Authentication authentication) {

        RefundStatus status = request.status();

        RefundRequest refund = refundService.review(
                id, status, authentication.getName());

        return RefundMapper.toResponse(refund);
    }

    @PutMapping("/{id}/complete-demo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN', 'FINANCE_ACCOUNTANT')")
    public RefundResponse completeDemo(@PathVariable Long id) {
        return RefundMapper.toResponse(refundService.completeDemo(id));
    }

    private boolean hasFinancialAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> Set.of(
                        "ADMIN",
                        "SUPER_ADMIN",
                        "FINANCE_ACCOUNTANT"
                ).contains(granted.getAuthority()));
    }
}
