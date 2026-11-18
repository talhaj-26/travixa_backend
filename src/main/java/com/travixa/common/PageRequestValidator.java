package com.travixa.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageRequestValidator {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;

    private PageRequestValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static Pageable validate(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? DEFAULT_PAGE_SIZE : size;

        if (safeSize > MAX_PAGE_SIZE) {
            safeSize = MAX_PAGE_SIZE;
        }

        return PageRequest.of(safePage, safeSize);
    }

    public static Pageable validate(Integer page, Integer size, int defaultSize, int maxSize) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? defaultSize : size;

        if (safeSize > maxSize) {
            safeSize = maxSize;
        }

        return PageRequest.of(safePage, safeSize);
    }
}
