package com.travixa.common;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SearchNormalizer {
    private SearchNormalizer() {
        throw new IllegalStateException("Utility class");
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return normalized;
    }

    public static String[] tokenize(String value) {
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return new String[0];
        }

        return Arrays.stream(normalized.split(" "))
                .filter(token -> !token.isBlank())
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    public static boolean containsToken(String search, String token) {
        Objects.requireNonNull(search, "search");
        Objects.requireNonNull(token, "token");
        String normalizedSearch = normalize(search);
        String normalizedToken = normalize(token);
        return normalizedSearch.contains(normalizedToken);
    }
}
