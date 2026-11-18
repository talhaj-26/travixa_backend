package com.travixa.dto.support_platform;

import com.travixa.enums.ContactInquiryStatus;

import java.time.Instant;

public record ContactInquiryResponse(
        Long id,
        String name,
        String email,
        String phoneNumber,
        String subject,
        String message,
        ContactInquiryStatus status,
        Instant createdAt,
        Instant respondedAt) {
}
