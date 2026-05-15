package com.iss.fitness.web.dto.workout;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateWorkoutSetRequest(
    @Min(value = 0, message = "Reps must be positive or zero")
    Integer reps,

    @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be positive or zero")
    BigDecimal weight,

    @Size(max = 80, message = "Last week value must be at most 80 characters")
    String lastWeek,

    Boolean completed
) {
}