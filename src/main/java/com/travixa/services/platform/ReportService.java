package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class ReportService {

    public String generateReport(String name) {
        return "Report generated: " + name;
    }
}
