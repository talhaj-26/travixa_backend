package com.travixa.controllers;

import com.travixa.services.platform.MobileCompanionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile-companion")
public class MobileCompanionController {

    private final MobileCompanionService mobileCompanionService;

    public MobileCompanionController(MobileCompanionService mobileCompanionService) {
        this.mobileCompanionService = mobileCompanionService;
    }

    @GetMapping("/status")
    public String status() {
        return mobileCompanionService.mobileStatus();
    }
}
