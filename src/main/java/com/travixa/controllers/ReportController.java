package com.travixa.controllers;

import com.travixa.dto.intelligence.ReportResponse;
import com.travixa.services.platform.ReportService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ReportResponse generate(@RequestParam @NotBlank @Size(max = 120) String name) {
        return new ReportResponse(name, reportService.generateReport(name));
    }
}
