package com.travixa.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public final class ReferenceNumberGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd", Locale.ROOT);

    private ReferenceNumberGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate(String prefix) {
        String normalizedPrefix = (prefix == null || prefix.isBlank()) ? "REF" : prefix.trim().toUpperCase(Locale.ROOT);
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
        return normalizedPrefix + "-" + datePart + "-" + uuidPart;
    }
}
