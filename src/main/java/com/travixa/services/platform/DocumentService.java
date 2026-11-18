package com.travixa.services.platform;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    public String prepareDocument(String title) {
        return "Document prepared: " + title;
    }
}
