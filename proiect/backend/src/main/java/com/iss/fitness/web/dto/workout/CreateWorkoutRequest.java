package com.iss.fitness.web.dto.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkoutRequest(
    @NotBlank(message = "Workout day is required")
    @Size(max = 20, message = "Workout day must be at most 20 characters")
    String day,

    @NotBlank(message = "Muscle group is required")
    @Size(max = 120, message = "Muscle group must be at most 120 characters")
    String muscle,

    @Size(max = 20, message = "Color must be at most 20 characters")
    String color
) {
}