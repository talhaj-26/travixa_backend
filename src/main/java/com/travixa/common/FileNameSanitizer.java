package com.travixa.common;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class FileNameSanitizer {
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9._-]");
    private static final int MAX_LENGTH = 120;

    private FileNameSanitizer() {
        throw new IllegalStateException("Utility class");
    }

    public static String sanitize(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "document";
        }

        String normalized = Normalizer.normalize(fileName.trim(), Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_");

        normalized = INVALID_CHARS.matcher(normalized).replaceAll("_");
        normalized = normalized.replaceAll("_+", "_");
        normalized = normalized.replaceAll("^_+|_+$", "");

        if (normalized.isBlank()) {
            normalized = "document";
        }

        if (normalized.length() > MAX_LENGTH) {
            normalized = normalized.substring(0, MAX_LENGTH).replaceAll("_+$", "");
        }

        return normalized.toLowerCase(Locale.ROOT);
    }
}
