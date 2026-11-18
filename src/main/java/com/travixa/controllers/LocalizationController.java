package com.travixa.controllers;

import com.travixa.services.platform.LocalizationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/localization")
public class LocalizationController {

    private final LocalizationService localizationService;

    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @GetMapping
    public String localize(
            @RequestParam @NotBlank @Size(max = 4000) String value,
            @RequestParam @NotBlank @Size(max = 35) String locale) {
        return localizationService.localize(value, locale);
    }
}
