package com.travixa.controllers;

import com.travixa.dto.intelligence.WeatherResponse;
import com.travixa.entity.platform_cms_support.WeatherSnapshot;
import com.travixa.services.platform.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public List<WeatherResponse> findAll() {
        return weatherService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public WeatherResponse findById(@PathVariable Long id) {
        return weatherService.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Weather snapshot not found"));
    }

    private WeatherResponse toResponse(WeatherSnapshot snapshot) {
        return new WeatherResponse(
                snapshot.getId(),
                snapshot.getDestination() == null ? null : snapshot.getDestination().getId(),
                snapshot.getLocationName(),
                snapshot.getTemperatureCelsius(),
                snapshot.getConditionCode(),
                snapshot.getObservedAt(),
                snapshot.getExpiresAt());
    }
}
