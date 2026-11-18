package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    public String summarizeMetrics() {
        return "Analytics summary generated.";
    }
}
