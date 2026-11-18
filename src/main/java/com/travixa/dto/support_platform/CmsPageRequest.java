package com.travixa.dto.support_platform;

import com.travixa.enums.ContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CmsPageRequest(
        @NotBlank @Size(max = 180) String slug,
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 30000) String content,
        Long languageId,
        @NotNull ContentStatus status) {
}
