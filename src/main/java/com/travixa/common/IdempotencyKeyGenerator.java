package com.travixa.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class IdempotencyKeyGenerator {
    private IdempotencyKeyGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static String generateFrom(String value) {
        if (value == null || value.isBlank()) {
            return generate();
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString();
        }
    }
}
