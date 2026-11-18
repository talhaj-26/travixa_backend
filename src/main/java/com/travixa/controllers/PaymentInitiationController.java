
package com.travixa.controllers;

import com.travixa.dto.payment.PaymentInitiationRequest;
import com.travixa.dto.payment.PaymentInitiationResponse;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.services.payment.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentInitiationController {

    private final PaymentService paymentService;

    public PaymentInitiationController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInitiationResponse initiate(
            @Valid @RequestBody PaymentInitiationRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Payment payment = paymentService.initiate(
                request, authentication.getName());

        return new PaymentInitiationResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getReferenceNumber(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMethod(),
                payment.getStatus(),
                false,
                payment.getMethod() == com.travixa.enums.PaymentMethod.DEMO,
                payment.getMethod() == com.travixa.enums.PaymentMethod.DEMO
                        ? "Demo payment completed and recorded. No external gateway transaction occurred."
                        : "Payment record created. No gateway is configured; payment has not been completed."
        );
    }
}
