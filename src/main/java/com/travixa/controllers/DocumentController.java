package com.travixa.controllers;

import com.travixa.dto.intelligence.DocumentResponse;
import com.travixa.services.platform.DocumentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/prepare")
    public DocumentResponse prepare(@RequestParam @NotBlank @Size(max = 200) String title) {
        return new DocumentResponse(title, documentService.prepareDocument(title));
    }
}
