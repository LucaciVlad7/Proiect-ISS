package com.iss.fitness.web.dto.workout;

import java.util.List;
import java.util.UUID;

public record WorkoutExerciseResponse(
    UUID id,
    UUID exerciseId,
    String name,
    String pb,
    List<WorkoutSetResponse> sets
) {
}