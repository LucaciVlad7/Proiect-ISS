package com.iss.fitness.repository;

import com.iss.fitness.domain.exercise.Exercise;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Exercise> findByNameIgnoreCase(String name);

    List<Exercise> findAllByOrderByNameAsc();
}