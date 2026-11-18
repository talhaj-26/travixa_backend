package com.travixa.controllers;

import com.travixa.dto.intelligence.AiTripPlanRequest;
import com.travixa.dto.intelligence.AiTripPlanResponse;
import com.travixa.services.platform.AiTripPlannerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-trips")
public class AiTripPlannerController {

    private final AiTripPlannerService aiTripPlannerService;

    public AiTripPlannerController(AiTripPlannerService aiTripPlannerService) {
        this.aiTripPlannerService = aiTripPlannerService;
    }

    @PostMapping("/plan")
    public AiTripPlanResponse planTrip(@Valid @RequestBody AiTripPlanRequest request) {
        return new AiTripPlanResponse(aiTripPlannerService.planTrip(request.destination()));
    }
}
