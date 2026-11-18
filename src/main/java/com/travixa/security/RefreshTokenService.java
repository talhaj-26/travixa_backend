package com.travixa.security;

import com.travixa.entity.identity_access.RefreshToken;
import com.travixa.entity.identity_access.User;
import com.travixa.exceptions.UnauthorizedException;
import com.travixa.repository.identity.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Base64;

@Service
@Transactional
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationMillis;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-expiration}") long refreshExpirationMillis) {
        if (refreshExpirationMillis <= 0) {
            throw new IllegalStateException("JWT refresh expiration must be positive");
        }
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    public String issue(User user) {
        byte[] tokenBytes = new byte[48];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshExpirationMillis));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    public RotatedToken rotate(String rawToken) {
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .filter(token -> !token.isRevoked() && token.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired refresh token"));

        current.setRevoked(true);
        String replacement = issue(current.getUser());
        return new RotatedToken(current.getUser(), replacement);
    }

    public void revoke(String rawToken, String username) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .filter(refreshToken -> refreshToken.getUser().getUserName().equals(username))
                .orElseThrow(
                        () -> new UnauthorizedException("Refresh token does not belong to the authenticated user"));
        token.setRevoked(true);
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    public record RotatedToken(User user, String refreshToken) {
    }
}
