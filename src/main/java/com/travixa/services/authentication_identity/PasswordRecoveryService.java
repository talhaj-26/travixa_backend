package com.travixa.services.authentication_identity;

import com.travixa.dto.auth.ForgotPasswordRequest;
import com.travixa.dto.auth.ResetPasswordRequest;
import com.travixa.entity.identity_access.PasswordResetToken;
import com.travixa.entity.identity_access.User;
import com.travixa.repository.identity.PasswordResetTokenRepository;
import com.travixa.repository.identity.RefreshTokenRepository;
import com.travixa.repository.identity.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@Transactional(readOnly = true)
public class PasswordRecoveryService {
    private static final Logger log = LoggerFactory.getLogger(PasswordRecoveryService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long TOKEN_LIFETIME_SECONDS = 900;

    private final UserRepository users;
    private final PasswordResetTokenRepository resetTokens;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenders;
    private final String frontendUrl;
    private final String senderAddress;

    public PasswordRecoveryService(UserRepository users, PasswordResetTokenRepository resetTokens,
                                  RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder,
                                  ObjectProvider<JavaMailSender> mailSenders,
                                  @Value("${app.frontend.url:http://localhost:4200}") String frontendUrl,
                                  @Value("${spring.mail.username:}") String senderAddress) {
        this.users = users;
        this.resetTokens = resetTokens;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.mailSenders = mailSenders;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.senderAddress = senderAddress;
    }

    @Transactional
    public boolean requestReset(ForgotPasswordRequest request) {
        JavaMailSender mailSender = mailSenders.getIfAvailable();
        if (mailSender == null) return false;

        users.findByEmailIgnoreCase(request.email().trim())
                .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
                .ifPresent(user -> issueAndSend(user, mailSender));
        return true;
    }

    public void validateToken(String rawToken) {
        findValidToken(rawToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = findValidToken(request.resetToken());
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        token.setConsumedAt(Instant.now());
        resetTokens.findAllByUser_UserNameAndConsumedAtIsNull(user.getUserName())
                .forEach(item -> item.setConsumedAt(Instant.now()));
        refreshTokens.deleteAllByUser_UserName(user.getUserName());
    }

    private void issueAndSend(User user, JavaMailSender mailSender) {
        String rawToken = newResetToken();
        Instant now = Instant.now();

        resetTokens.findAllByUser_UserNameAndConsumedAtIsNull(user.getUserName())
                .forEach(item -> item.setConsumedAt(now));

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(now.plusSeconds(TOKEN_LIFETIME_SECONDS));
        resetTokens.save(token);

        String link = frontendUrl + "/reset-password?token=" + rawToken;
        SimpleMailMessage message = new SimpleMailMessage();
        if (!senderAddress.isBlank()) message.setFrom(senderAddress);
        message.setTo(user.getEmail());
        message.setSubject("Reset your TRAVIXA password");
        message.setText("We received a request to reset your TRAVIXA password.\n\n"
                + "Use this single-use link within 15 minutes:\n" + link + "\n\n"
                + "If you did not request this, you can ignore this email.");
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            // Keep the public response indistinguishable for known and unknown emails.
            token.setConsumedAt(Instant.now());
            log.warn("Password recovery email delivery failed");
        }
    }

    private PasswordResetToken findValidToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset link is invalid or expired");
        }
        PasswordResetToken token = resetTokens.findByTokenHashAndConsumedAtIsNull(hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Reset link is invalid or expired"));
        if (!token.getExpiresAt().isAfter(Instant.now())
                || !Boolean.TRUE.equals(token.getUser().getEnabled())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset link is invalid or expired");
        }
        return token;
    }

    private String newResetToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
