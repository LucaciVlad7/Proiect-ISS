package com.iss.fitness.web;

import com.iss.fitness.service.ExerciseCatalogService;
import com.iss.fitness.web.dto.exercise.ExerciseResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseCatalogService exerciseCatalogService;

    public ExerciseController(ExerciseCatalogService exerciseCatalogService) {
        this.exerciseCatalogService = exerciseCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises() {
        return ResponseEntity.ok(exerciseCatalogService.getAllExercises());
    }
}