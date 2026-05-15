package com.iss.fitness.web;

import com.iss.fitness.service.ExerciseCatalogService;
import com.iss.fitness.web.dto.exercise.ExerciseRequest;
import com.iss.fitness.web.dto.exercise.ExerciseResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/exercises")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExerciseController {

    private final ExerciseCatalogService exerciseCatalogService;

    public AdminExerciseController(ExerciseCatalogService exerciseCatalogService) {
        this.exerciseCatalogService = exerciseCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises() {
        return ResponseEntity.ok(exerciseCatalogService.getAllExercises());
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> createExercise(@Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseCatalogService.createExercise(request));
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<ExerciseResponse> updateExercise(
        @PathVariable UUID exerciseId,
        @Valid @RequestBody ExerciseRequest request
    ) {
        return ResponseEntity.ok(exerciseCatalogService.updateExercise(exerciseId, request));
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(@PathVariable UUID exerciseId) {
        exerciseCatalogService.deleteExercise(exerciseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seed")
    public ResponseEntity<Map<String, Integer>> seedExercises() {
        return ResponseEntity.ok(Map.of("inserted", exerciseCatalogService.seedDefaultExercises()));
    }
}