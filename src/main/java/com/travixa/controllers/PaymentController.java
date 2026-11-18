package com.travixa.controllers;

import com.travixa.dto.payment.PaymentResponse;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.mapper.PaymentMapper;
import com.travixa.services.payment.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll(Authentication authentication) {
        List<Payment> payments = hasFinancialAuthority(authentication)
                ? paymentService.findAll()
                : paymentService.findAllByCustomer(authentication.getName());
        return payments.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PaymentResponse findById(@PathVariable Long id, Authentication authentication) {
        return (hasFinancialAuthority(authentication)
                ? paymentService.findById(id)
                : paymentService.findByIdAndCustomer(id, authentication.getName()))
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    private boolean hasFinancialAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> java.util.Set.of("ADMIN", "SUPER_ADMIN", "FINANCE_ACCOUNTANT")
                        .contains(granted.getAuthority()));
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentMapper.toResponse(payment);
    }
}
