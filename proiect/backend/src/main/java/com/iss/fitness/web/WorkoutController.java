package com.iss.fitness.web;

import com.iss.fitness.domain.user.User;
import com.iss.fitness.service.WorkoutService;
import com.iss.fitness.web.dto.workout.AddExerciseToWorkoutRequest;
import com.iss.fitness.web.dto.workout.CreateWorkoutRequest;
import com.iss.fitness.web.dto.workout.ReorderExercisesRequest;
import com.iss.fitness.web.dto.workout.UpdateWorkoutSetRequest;
import com.iss.fitness.web.dto.workout.WorkoutResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getWorkouts(Authentication authentication) {
        return ResponseEntity.ok(workoutService.getWorkouts((User) authentication.getPrincipal()));
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(
        Authentication authentication,
        @Valid @RequestBody CreateWorkoutRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(workoutService.createWorkout((User) authentication.getPrincipal(), request));
    }

    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<WorkoutResponse> addExercise(
        Authentication authentication,
        @PathVariable UUID workoutId,
        @Valid @RequestBody AddExerciseToWorkoutRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(workoutService.addExercise((User) authentication.getPrincipal(), workoutId, request));
    }

    @DeleteMapping("/{workoutId}/exercises/{workoutExerciseId}")
    public ResponseEntity<WorkoutResponse> deleteExercise(
        Authentication authentication,
        @PathVariable UUID workoutId,
        @PathVariable UUID workoutExerciseId
    ) {
        return ResponseEntity.ok(
            workoutService.deleteExercise((User) authentication.getPrincipal(), workoutId, workoutExerciseId)
        );
    }

    @PostMapping("/{workoutId}/exercises/reorder")
    public ResponseEntity<WorkoutResponse> reorderExercises(
        Authentication authentication,
        @PathVariable UUID workoutId,
        @Valid @RequestBody ReorderExercisesRequest request
    ) {
        return ResponseEntity.ok(
            workoutService.reorderExercises((User) authentication.getPrincipal(), workoutId, request)
        );
    }

    @PostMapping("/{workoutId}/exercises/{workoutExerciseId}/sets")
    public ResponseEntity<WorkoutResponse> addSet(
        Authentication authentication,
        @PathVariable UUID workoutId,
        @PathVariable UUID workoutExerciseId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(workoutService.addSet((User) authentication.getPrincipal(), workoutId, workoutExerciseId));
    }

    @PatchMapping("/{workoutId}/exercises/{workoutExerciseId}/sets/{setId}")
    public ResponseEntity<WorkoutResponse> updateSet(
        Authentication authentication,
        @PathVariable UUID workoutId,
        @PathVariable UUID workoutExerciseId,
        @PathVariable UUID setId,
        @Valid @RequestBody UpdateWorkoutSetRequest request
    ) {
        return ResponseEntity.ok(
            workoutService.updateSet((User) authentication.getPrincipal(), workoutId, workoutExerciseId, setId, request)
        );
    }

    @DeleteMapping("/{workoutId}/exercises/{workoutExerciseId}/sets/{setId}")
    public ResponseEntity<WorkoutResponse> deleteSet(
        Authentication authentication,
        @PathVariable UUID workoutId,
        @PathVariable UUID workoutExerciseId,
        @PathVariable UUID setId
    ) {
        return ResponseEntity.ok(
            workoutService.deleteSet((User) authentication.getPrincipal(), workoutId, workoutExerciseId, setId)
        );
    }
}