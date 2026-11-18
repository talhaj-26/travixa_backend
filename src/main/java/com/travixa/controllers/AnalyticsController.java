package com.travixa.controllers;

import com.travixa.dto.intelligence.AnalyticsResponse;
import com.travixa.services.platform.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public AnalyticsResponse getSummary() {
        return new AnalyticsResponse(analyticsService.summarizeMetrics());
    }
}
