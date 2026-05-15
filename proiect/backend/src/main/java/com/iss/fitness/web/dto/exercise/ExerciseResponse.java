package com.iss.fitness.web.dto.exercise;

import java.util.UUID;

public record ExerciseResponse(
    UUID id,
    String name,
    String primaryMuscle,
    String equipment,
    String description
) {
}