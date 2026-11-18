package com.travixa.services.authentication_identity;

import com.travixa.dto.auth.LoginRequest;
import com.travixa.dto.auth.RegisterRequest;
import com.travixa.dto.auth.TokenResponse;
import com.travixa.entity.identity_access.Role;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.ERole;
import com.travixa.exceptions.DuplicateResourceException;
import com.travixa.exceptions.UnauthorizedException;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.identity.RoleRepository;
import com.travixa.security.JwtUtil;
import com.travixa.security.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final long tokenExpirationMillis;

    public AuthService(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            @Value("${app.jwt.expiration}") long tokenExpirationMillis) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.tokenExpirationMillis = tokenExpirationMillis;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUserNameIgnoreCase(username)) {
            throw new DuplicateResourceException("Username is already registered");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = new User(username, email, passwordEncoder.encode(request.password()));
        user.setUserFirstName(normalizeOptional(request.firstName()));
        user.setUserLastName(normalizeOptional(request.lastName()));
        Role customerRole = roleRepository.findByName(ERole.CUSTOMER.name())
                .orElseThrow(() -> new IllegalStateException("Customer role has not been initialized"));
        user.setRoles(Set.of(customerRole));

        return toTokenResponse(userRepository.save(user));
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        String username = request.username().trim();
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, request.password()));
        } catch (AuthenticationException exception) {
            throw new UnauthorizedException("Invalid username or password");
        }

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        return toTokenResponse(user);
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken) {
        RefreshTokenService.RotatedToken rotatedToken = refreshTokenService.rotate(rawRefreshToken);
        return toTokenResponse(rotatedToken.user(), rotatedToken.refreshToken());
    }

    @Transactional
    public void logout(String rawRefreshToken, String username) {
        refreshTokenService.revoke(rawRefreshToken, username);
    }

    private TokenResponse toTokenResponse(User user) {
        return toTokenResponse(user, refreshTokenService.issue(user));
    }

    private TokenResponse toTokenResponse(User user, String refreshToken) {
        return new TokenResponse(
                jwtUtil.generateTokenFromUsername(user),
                "Bearer",
                tokenExpirationMillis / 1_000,
                refreshToken,
                user.getUserName(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .sorted()
                        .toList());
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
