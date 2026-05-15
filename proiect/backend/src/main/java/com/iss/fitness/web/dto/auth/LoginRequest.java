package com.iss.fitness.web.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {
}
