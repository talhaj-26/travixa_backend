package com.travixa.services.authentication_identity;

import com.travixa.entity.identity_access.User;
import com.travixa.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtUtil jwtUtil;

    public JwtTokenService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateAccessToken(User user) {
        return jwtUtil.generateTokenFromUsername(user);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
}
