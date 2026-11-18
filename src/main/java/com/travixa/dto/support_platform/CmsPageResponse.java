package com.travixa.dto.support_platform;

import com.travixa.enums.ContentStatus;

import java.time.Instant;

public record CmsPageResponse(
        Long id,
        String slug,
        String title,
        String content,
        Long languageId,
        ContentStatus status,
        Instant publishedAt) {
}
