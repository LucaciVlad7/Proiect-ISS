package com.iss.fitness.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Size(min = 8, max = 120) String password,
    @NotBlank @Size(min = 2, max = 80) String name
) {
}
