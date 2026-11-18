package com.travixa.common;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugGenerator {
    private static final Pattern NON_ALNUM_PATTERN = Pattern.compile("[^a-z0-9]+");

    private SlugGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate(String value) {
        if (value == null || value.isBlank()) {
            return "untitled";
        }

        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);

        String slug = NON_ALNUM_PATTERN.matcher(normalized).replaceAll("-");

        slug = slug.replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        return slug.isBlank() ? "untitled" : slug;
    }
}