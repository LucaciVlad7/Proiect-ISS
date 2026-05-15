package com.iss.fitness.web.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExerciseRequest(
    @NotBlank(message = "Exercise name is required")
    @Size(max = 120, message = "Exercise name must be at most 120 characters")
    String name,

    @Size(max = 120, message = "Primary muscle must be at most 120 characters")
    String primaryMuscle,

    @Size(max = 120, message = "Equipment must be at most 120 characters")
    String equipment,

    @Size(max = 500, message = "Description must be at most 500 characters")
    String description
) {
}