package com.iss.fitness.web.dto.auth;

import java.util.UUID;

public record AuthResponse(
    String token,
    UUID userId,
    String username,
    String name,
    String role
) {
}
