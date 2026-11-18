package com.travixa.dto.support_platform;

public record LanguageResponse(
        Long id,
        String code,
        String displayName,
        String nativeName,
        boolean enabled,
        boolean defaultLanguage) {
}
