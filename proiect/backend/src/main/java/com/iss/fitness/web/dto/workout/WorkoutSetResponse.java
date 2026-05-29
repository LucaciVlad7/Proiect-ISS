package com.iss.fitness.web.dto.workout;

import java.math.BigDecimal;
import java.util.UUID;

public record WorkoutSetResponse(
    UUID id,
    Integer reps,
    BigDecimal weight,
    String lastWeek,
    BigDecimal lastWeekWeight,
    Integer lastWeekReps,
    boolean completed
) {
}