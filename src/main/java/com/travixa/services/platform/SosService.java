package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class SosService {

    public String createAlert(String message) {
        return "SOS alert dispatched: " + message;
    }
}
