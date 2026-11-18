package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class AiTripPlannerService {

    public String planTrip(String destination) {
        return "AI trip plan for " + destination + " is ready.";
    }
}
