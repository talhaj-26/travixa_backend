package com.travixa.dto.support_platform;

import com.travixa.enums.ContentStatus;

import java.time.Instant;

public record CmsBannerResponse(
        Long id,
        String title,
        String imageStorageKey,
        String targetUrl,
        int displayOrder,
        ContentStatus status,
        Long languageId,
        Instant startsAt,
        Instant endsAt) {
}
