package com.travixa.mapper;

import com.travixa.dto.support_platform.CmsPageRequest;
import com.travixa.dto.support_platform.CmsPageResponse;
import com.travixa.entity.platform_cms_support.CmsPage;
import com.travixa.entity.platform_cms_support.Language;

import java.util.Objects;

public final class CmsPageMapper {

    private CmsPageMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CmsPage toEntity(CmsPageRequest request, Language language) {
        CmsPage page = new CmsPage();
        page.setLanguage(Objects.requireNonNull(language, "language must not be null"));
        page.setSlug(request.slug());
        page.setTitle(request.title());
        page.setContent(request.content());
        page.setStatus(request.status());
        return page;
    }

    public static CmsPageResponse toResponse(CmsPage page) {
        return new CmsPageResponse(page.getId(), page.getSlug(), page.getTitle(), page.getContent(),
                page.getLanguage().getId(), page.getStatus(), page.getPublishedAt());
    }
}
