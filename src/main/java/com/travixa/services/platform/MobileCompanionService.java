package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class MobileCompanionService {

    public String mobileStatus() {
        return "Companion service online.";
    }
}
