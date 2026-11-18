package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class MapRouteService {

    public String routeSummary(String origin, String destination) {
        return origin + " to " + destination;
    }
}
