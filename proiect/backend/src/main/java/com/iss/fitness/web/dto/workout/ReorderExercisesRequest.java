package com.iss.fitness.web.dto.workout;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record ReorderExercisesRequest(
    @NotEmpty(message = "Exercise IDs list cannot be empty")
    List<UUID> exerciseIds
) {
}
