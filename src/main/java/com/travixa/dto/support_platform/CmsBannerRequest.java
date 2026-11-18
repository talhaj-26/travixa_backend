package com.travixa.dto.support_platform;

import com.travixa.enums.ContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CmsBannerRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 512) String imageStorageKey,
        @Size(max = 2048) String targetUrl,
        @PositiveOrZero int displayOrder,
        @NotNull ContentStatus status,
        Long languageId,
        Instant startsAt,
        Instant endsAt) {
}
