package com.iss.fitness.web.dto.workout;

import java.util.List;
import java.util.UUID;

public record WorkoutResponse(
    UUID id,
    String day,
    String muscle,
    String color,
    List<WorkoutExerciseResponse> exercises
) {
}