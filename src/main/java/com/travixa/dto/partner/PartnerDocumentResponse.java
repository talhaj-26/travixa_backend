package com.travixa.dto.partner;

import com.travixa.enums.PartnerDocumentStatus;

public record PartnerDocumentResponse(
        Long id,
        Long applicationId,
        String documentType,
        String originalFilename,
        String contentType,
        PartnerDocumentStatus status) {
}
