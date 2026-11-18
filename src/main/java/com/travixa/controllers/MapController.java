package com.travixa.controllers;

import com.travixa.dto.intelligence.RouteResponse;
import com.travixa.services.platform.MapRouteService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/routes")
public class MapController {

    private final MapRouteService mapRouteService;

    public MapController(MapRouteService mapRouteService) {
        this.mapRouteService = mapRouteService;
    }

    @GetMapping("/summary")
    public RouteResponse summarize(
            @RequestParam @NotBlank @Size(max = 160) String origin,
            @RequestParam @NotBlank @Size(max = 160) String destination) {
        return new RouteResponse(origin, destination, mapRouteService.routeSummary(origin, destination));
    }
}
