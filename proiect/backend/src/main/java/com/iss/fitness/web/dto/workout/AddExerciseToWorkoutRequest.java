package com.iss.fitness.web.dto.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddExerciseToWorkoutRequest(
    @NotBlank(message = "Exercise name is required")
    @Size(min = 2, max = 120, message = "Exercise name must be between 2 and 120 characters")
    String exerciseName
) {
}