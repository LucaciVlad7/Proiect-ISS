package com.iss.fitness.repository;

import com.iss.fitness.domain.workout.WorkoutExercise;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, UUID> {

    @EntityGraph(attributePaths = {"workout", "workout.owner", "exercise", "sets"})
    Optional<WorkoutExercise> findById(UUID id);

    boolean existsByExerciseId(UUID exerciseId);
}