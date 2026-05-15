package com.iss.fitness.repository;

import com.iss.fitness.domain.workout.WorkoutSet;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {

    @EntityGraph(attributePaths = {"workoutExercise", "workoutExercise.workout", "workoutExercise.workout.owner"})
    Optional<WorkoutSet> findById(UUID id);
}