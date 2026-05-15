package com.iss.fitness.repository;

import com.iss.fitness.domain.workout.Workout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

    @EntityGraph(attributePaths = {"owner", "exercises", "exercises.exercise", "exercises.sets"})
    List<Workout> findAllByOwnerIdOrderByCreatedAtAsc(UUID ownerId);

    @EntityGraph(attributePaths = {"owner", "exercises", "exercises.exercise", "exercises.sets"})
    Optional<Workout> findByIdAndOwnerId(UUID id, UUID ownerId);

    void deleteAllByOwnerId(UUID ownerId);
}