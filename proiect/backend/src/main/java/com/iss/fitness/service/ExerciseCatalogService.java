package com.iss.fitness.service;

import com.iss.fitness.domain.exercise.Exercise;
import com.iss.fitness.exception.ResourceNotFoundException;
import com.iss.fitness.repository.ExerciseRepository;
import com.iss.fitness.repository.WorkoutExerciseRepository;
import com.iss.fitness.web.dto.exercise.ExerciseRequest;
import com.iss.fitness.web.dto.exercise.ExerciseResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseCatalogService {

    private static final List<ExerciseRequest> DEFAULT_EXERCISES = List.of(
        new ExerciseRequest("Bench Press", "Chest", "Barbell", "Classic chest press for upper-body strength"),
        new ExerciseRequest("Incline Dumbbell Press", "Chest", "Dumbbells", "Incline pressing variation for upper chest"),
        new ExerciseRequest("Pull-Up", "Back", "Bodyweight", "Vertical pull focused on lats and upper back"),
        new ExerciseRequest("Barbell Row", "Back", "Barbell", "Horizontal row for back thickness"),
        new ExerciseRequest("Overhead Press", "Shoulders", "Barbell", "Standing press for shoulders and triceps"),
        new ExerciseRequest("Lateral Raise", "Shoulders", "Dumbbells", "Isolation movement for side delts"),
        new ExerciseRequest("Barbell Squat", "Legs", "Barbell", "Compound squat for quads and glutes"),
        new ExerciseRequest("Romanian Deadlift", "Hamstrings", "Barbell", "Hip hinge for hamstrings and glutes"),
        new ExerciseRequest("Leg Press", "Legs", "Machine", "Machine-based quad-dominant press"),
        new ExerciseRequest("Biceps Curl", "Biceps", "Dumbbells", "Arm isolation exercise for biceps"),
        new ExerciseRequest("Triceps Pushdown", "Triceps", "Cable", "Cable isolation for triceps"),
        new ExerciseRequest("Cable Crunch", "Core", "Cable", "Weighted abdominal crunch variation")
    );

    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public ExerciseCatalogService(ExerciseRepository exerciseRepository, WorkoutExerciseRepository workoutExerciseRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getAllExercises() {
        return exerciseRepository.findAllByOrderByNameAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest request) {
        String normalizedName = normalizeRequired(request.name(), "Exercise name");
        if (exerciseRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Exercise already exists");
        }

        Exercise exercise = new Exercise();
        applyRequest(exercise, request, normalizedName);
        return toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public ExerciseResponse updateExercise(UUID exerciseId, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        String normalizedName = normalizeRequired(request.name(), "Exercise name");
        exerciseRepository.findByNameIgnoreCase(normalizedName)
            .filter(existing -> !existing.getId().equals(exerciseId))
            .ifPresent(existing -> {
                throw new IllegalArgumentException("Exercise already exists");
            });

        applyRequest(exercise, request, normalizedName);
        return toResponse(exercise);
    }

    @Transactional
    public void deleteExercise(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

        if (workoutExerciseRepository.existsByExerciseId(exerciseId)) {
            throw new IllegalArgumentException("Exercise is already used in workouts");
        }

        exerciseRepository.delete(exercise);
    }

    @Transactional
    public int seedDefaultExercises() {
        int inserted = 0;
        for (ExerciseRequest exerciseRequest : DEFAULT_EXERCISES) {
            if (exerciseRepository.existsByNameIgnoreCase(exerciseRequest.name())) {
                continue;
            }

            Exercise exercise = new Exercise();
            applyRequest(exercise, exerciseRequest, exerciseRequest.name());
            exerciseRepository.save(exercise);
            inserted++;
        }
        return inserted;
    }

    @Transactional(readOnly = true)
    public Exercise getExercise(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
    }

    @Transactional
    public Exercise findOrCreateByName(String name) {
        String normalized = normalizeRequired(name, "Exercise name");
        return exerciseRepository.findByNameIgnoreCase(normalized)
            .orElseGet(() -> {
                Exercise exercise = new Exercise();
                exercise.setName(normalized);
                return exerciseRepository.save(exercise);
            });
    }

    private void applyRequest(Exercise exercise, ExerciseRequest request, String normalizedName) {
        exercise.setName(normalizedName);
        exercise.setPrimaryMuscle(normalizeOptional(request.primaryMuscle()));
        exercise.setEquipment(normalizeOptional(request.equipment()));
        exercise.setDescription(normalizeOptional(request.description()));
    }

    private ExerciseResponse toResponse(Exercise exercise) {
        return new ExerciseResponse(
            exercise.getId(),
            exercise.getName(),
            exercise.getPrimaryMuscle(),
            exercise.getEquipment(),
            exercise.getDescription()
        );
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}