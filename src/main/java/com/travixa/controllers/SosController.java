package com.travixa.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sos")
@PreAuthorize("isAuthenticated()")
public class SosController {

    @GetMapping("/status")
    public ProblemDetail getDispatchStatus() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SOS dispatch is unavailable because no dispatch provider is configured.");
        problem.setTitle("SOS dispatch unavailable");
        return problem;
    }
}
