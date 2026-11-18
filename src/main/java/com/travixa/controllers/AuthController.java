package com.travixa.controllers;

import com.travixa.dto.auth.LoginRequest;
import com.travixa.dto.auth.ForgotPasswordRequest;
import com.travixa.dto.auth.RefreshTokenRequest;
import com.travixa.dto.auth.RegisterRequest;
import com.travixa.dto.auth.ResetPasswordRequest;
import com.travixa.dto.auth.ResetTokenValidationRequest;
import com.travixa.dto.auth.TokenResponse;
import com.travixa.services.authentication_identity.AuthService;
import com.travixa.services.authentication_identity.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordRecoveryService passwordRecoveryService;

    public AuthController(AuthService authService, PasswordRecoveryService passwordRecoveryService) {
        this.authService = authService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @PostMapping({ "/signup", "/register" })
    public ResponseEntity<TokenResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping({ "/signin", "/login" })
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        if (!passwordRecoveryService.requestReset(request)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/validate-reset-token")
    public ResponseEntity<Void> validateResetToken(@Valid @RequestBody ResetTokenValidationRequest request) {
        passwordRecoveryService.validateToken(request.resetToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordRecoveryService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(request.refreshToken(), userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
