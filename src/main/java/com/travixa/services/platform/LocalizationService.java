package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class LocalizationService {

    public String localize(String value, String locale) {
        return value == null ? "" : value;
    }
}
