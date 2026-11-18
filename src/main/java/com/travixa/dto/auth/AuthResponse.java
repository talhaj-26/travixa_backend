package com.travixa.dto.auth;

import java.util.List;

public record AuthResponse(String username, String email, List<String> roles) {
}
