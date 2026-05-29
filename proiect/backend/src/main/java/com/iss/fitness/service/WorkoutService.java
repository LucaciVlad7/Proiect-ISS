package com.iss.fitness.service;

import com.iss.fitness.domain.exercise.Exercise;
import com.iss.fitness.domain.user.User;
import com.iss.fitness.domain.workout.Workout;
import com.iss.fitness.domain.workout.WorkoutExercise;
import com.iss.fitness.domain.workout.WorkoutSet;
import com.iss.fitness.exception.ResourceNotFoundException;
import com.iss.fitness.repository.WorkoutExerciseRepository;
import com.iss.fitness.repository.WorkoutRepository;
import com.iss.fitness.repository.WorkoutSetRepository;
import com.iss.fitness.web.dto.workout.AddExerciseToWorkoutRequest;
import com.iss.fitness.web.dto.workout.CreateWorkoutRequest;
import com.iss.fitness.web.dto.workout.ReorderExercisesRequest;
import com.iss.fitness.web.dto.workout.UpdateWorkoutSetRequest;
import com.iss.fitness.web.dto.workout.WorkoutExerciseResponse;
import com.iss.fitness.web.dto.workout.WorkoutResponse;
import com.iss.fitness.web.dto.workout.WorkoutSetResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkoutService {

    private static final List<String> DEFAULT_COLORS = List.of(
        "#3b82f6",
        "#8b5cf6",
        "#10b981",
        "#f59e0b",
        "#ef4444",
        "#ec4899",
        "#14b8a6"
    );

    private static final Set<String> VALID_DAYS = Set.of(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    );

    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final ExerciseCatalogService exerciseCatalogService;

    public WorkoutService(
        WorkoutRepository workoutRepository,
        WorkoutExerciseRepository workoutExerciseRepository,
        WorkoutSetRepository workoutSetRepository,
        ExerciseCatalogService exerciseCatalogService
    ) {
        this.workoutRepository = workoutRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.exerciseCatalogService = exerciseCatalogService;
    }

    @Transactional
    public List<WorkoutResponse> getWorkouts(User user) {
        List<Workout> workouts = workoutRepository.findAllByOwnerIdOrderByCreatedAtAsc(user.getId());
        rollOverWorkoutsIfNeeded(workouts);
        return workouts.stream().map(this::toResponse).toList();
    }

    @Transactional
    public WorkoutResponse createWorkout(User user, CreateWorkoutRequest request) {
        Workout workout = new Workout();
        workout.setOwner(user);
        workout.setDay(normalizeDay(request.day()));
        workout.setMuscleGroup(normalizeRequired(request.muscle(), "Muscle group"));
        workout.setColor(resolveColor(user.getId(), request.color()));

        return toResponse(workoutRepository.save(workout));
    }

    @Transactional
    public WorkoutResponse addExercise(User user, UUID workoutId, AddExerciseToWorkoutRequest request) {
        Workout workout = getOwnedWorkout(user.getId(), workoutId);
        rollOverWorkoutSetsIfNeeded(workout);

        Exercise exercise = exerciseCatalogService.findOrCreateByName(request.exerciseName());

        boolean alreadyAdded = workout.getExercises().stream()
            .anyMatch(existing -> existing.getExercise().getId().equals(exercise.getId()));
        if (alreadyAdded) {
            throw new IllegalArgumentException("Exercise already added to workout");
        }

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setPosition(workout.getExercises().size() + 1);

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setPosition(1);
        workoutSet.setLastWeek("-");
        workoutSet.setWeekMarker(getCurrentWeekMarker());
        workoutSet.setCompleted(false);
        workoutExercise.getSets().add(workoutSet);

        workout.getExercises().add(workoutExercise);
        workoutRepository.save(workout);
        return getWorkout(user, workoutId);
    }

    @Transactional
    public WorkoutResponse deleteExercise(User user, UUID workoutId, UUID workoutExerciseId) {
        Workout workout = getOwnedWorkout(user.getId(), workoutId);
        rollOverWorkoutSetsIfNeeded(workout);
        WorkoutExercise workoutExercise = getOwnedWorkoutExercise(user.getId(), workoutId, workoutExerciseId);

        workout.getExercises().removeIf(existing -> existing.getId().equals(workoutExercise.getId()));
        resequenceExercises(workout);
        workoutRepository.save(workout);
        return getWorkout(user, workoutId);
    }

    @Transactional
    public WorkoutResponse reorderExercises(User user, UUID workoutId, ReorderExercisesRequest request) {
        Workout workout = getOwnedWorkout(user.getId(), workoutId);
        rollOverWorkoutSetsIfNeeded(workout);
        Set<UUID> workoutExerciseIds = workout.getExercises().stream()
            .map(WorkoutExercise::getId)
            .collect(java.util.stream.Collectors.toSet());

        if (!workoutExerciseIds.containsAll(request.exerciseIds())) {
            throw new IllegalArgumentException("Invalid exercise IDs for this workout");
        }

        List<WorkoutExercise> exercises = new java.util.ArrayList<>(workout.getExercises());
        for (int i = 0; i < request.exerciseIds().size(); i++) {
            UUID exerciseId = request.exerciseIds().get(i);
            int position = i + 1;
            exercises.stream()
                .filter(ex -> ex.getId().equals(exerciseId))
                .findFirst()
                .ifPresent(ex -> ex.setPosition(position));
        }

        workoutRepository.save(workout);
        return getWorkout(user, workoutId);
    }

    @Transactional
    public WorkoutResponse addSet(User user, UUID workoutId, UUID workoutExerciseId) {
        WorkoutExercise workoutExercise = getOwnedWorkoutExercise(user.getId(), workoutId, workoutExerciseId);
        rollOverWorkoutSetsIfNeeded(workoutExercise.getWorkout());

        WorkoutSet previousSet = workoutExercise.getSets().isEmpty()
            ? null
            : new ArrayList<>(workoutExercise.getSets()).get(workoutExercise.getSets().size() - 1);

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setPosition(workoutExercise.getSets().size() + 1);
        workoutSet.setReps(previousSet != null ? previousSet.getReps() : null);
        workoutSet.setWeight(previousSet != null ? previousSet.getWeight() : null);
        workoutSet.setLastWeek(previousSet != null ? previousSet.getLastWeek() : "-");
        workoutSet.setWeekMarker(getCurrentWeekMarker());
        workoutSet.setCompleted(false);

        workoutExercise.getSets().add(workoutSet);
        workoutExerciseRepository.save(workoutExercise);
        return getWorkout(user, workoutId);
    }

    @Transactional
    public WorkoutResponse updateSet(
        User user,
        UUID workoutId,
        UUID workoutExerciseId,
        UUID setId,
        UpdateWorkoutSetRequest request
    ) {
        WorkoutExercise workoutExercise = getOwnedWorkoutExercise(user.getId(), workoutId, workoutExerciseId);
        rollOverWorkoutSetsIfNeeded(workoutExercise.getWorkout());
        WorkoutSet workoutSet = workoutExercise.getSets().stream()
            .filter(existing -> existing.getId().equals(setId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Workout set not found"));

        if (request.reps() != null) {
            workoutSet.setReps(request.reps());
            // If this set's weight is the current PB, sync the PB reps too
            BigDecimal currentPb = workoutExercise.getPersonalBest();
            if (workoutSet.getWeight() != null && currentPb != null
                    && workoutSet.getWeight().compareTo(currentPb) == 0) {
                workoutExercise.setPersonalBestReps(request.reps());
                workoutExerciseRepository.save(workoutExercise);
            }
        }
        if (request.weight() != null) {
            workoutSet.setWeight(request.weight());
            BigDecimal newWeight = request.weight();
            if (newWeight.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal currentPb = workoutExercise.getPersonalBest();
                if (currentPb == null || newWeight.compareTo(currentPb) > 0) {
                    workoutExercise.setPersonalBest(newWeight);
                    workoutExercise.setPersonalBestReps(workoutSet.getReps());
                    workoutExerciseRepository.save(workoutExercise);
                }
            }
        }
        if (request.lastWeek() != null) {
            String trimmed = request.lastWeek().trim();
            workoutSet.setLastWeek(trimmed.isEmpty() ? "-" : trimmed);
        }
        if (request.completed() != null) {
            workoutSet.setCompleted(request.completed());
        }

        workoutSetRepository.save(workoutSet);
        return getWorkout(user, workoutId);
    }

    @Transactional
    public WorkoutResponse deleteSet(User user, UUID workoutId, UUID workoutExerciseId, UUID setId) {
        WorkoutExercise workoutExercise = getOwnedWorkoutExercise(user.getId(), workoutId, workoutExerciseId);
        rollOverWorkoutSetsIfNeeded(workoutExercise.getWorkout());

        boolean removed = workoutExercise.getSets().removeIf(existing -> existing.getId().equals(setId));
        if (!removed) {
            throw new ResourceNotFoundException("Workout set not found");
        }

        resequenceSets(workoutExercise);
        workoutExerciseRepository.save(workoutExercise);
        return getWorkout(user, workoutId);
    }

    @Transactional
    public WorkoutResponse getWorkout(User user, UUID workoutId) {
        Workout workout = getOwnedWorkout(user.getId(), workoutId);
        rollOverWorkoutSetsIfNeeded(workout);
        return toResponse(workout);
    }

    private Workout getOwnedWorkout(UUID ownerId, UUID workoutId) {
        return workoutRepository.findByIdAndOwnerId(workoutId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
    }

    private WorkoutExercise getOwnedWorkoutExercise(UUID ownerId, UUID workoutId, UUID workoutExerciseId) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Workout exercise not found"));

        boolean matchesOwner = workoutExercise.getWorkout().getOwner().getId().equals(ownerId);
        boolean matchesWorkout = workoutExercise.getWorkout().getId().equals(workoutId);
        if (!matchesOwner || !matchesWorkout) {
            throw new ResourceNotFoundException("Workout exercise not found");
        }

        return workoutExercise;
    }

    private WorkoutResponse toResponse(Workout workout) {
        return new WorkoutResponse(
            workout.getId(),
            workout.getDay(),
            workout.getMuscleGroup(),
            workout.getColor(),
            workout.getExercises().stream().map(this::toResponse).toList()
        );
    }

    private WorkoutExerciseResponse toResponse(WorkoutExercise workoutExercise) {
        return new WorkoutExerciseResponse(
            workoutExercise.getId(),
            workoutExercise.getExercise().getId(),
            workoutExercise.getExercise().getName(),
            formatPersonalBest(workoutExercise),
            workoutExercise.getSets().stream().map(this::toResponse).toList()
        );
    }

    private WorkoutSetResponse toResponse(WorkoutSet workoutSet) {
        return new WorkoutSetResponse(
            workoutSet.getId(),
            workoutSet.getReps(),
            workoutSet.getWeight(),
            workoutSet.getLastWeek(),
            workoutSet.getLastWeekWeight(),
            workoutSet.getLastWeekReps(),
            workoutSet.isCompleted()
        );
    }

    private String formatPersonalBest(WorkoutExercise workoutExercise) {
        if (workoutExercise.getPersonalBest() != null
                && workoutExercise.getPersonalBest().compareTo(BigDecimal.ZERO) > 0) {
            String weightPart = workoutExercise.getPersonalBest().stripTrailingZeros().toPlainString() + "kg";
            Integer pbReps = workoutExercise.getPersonalBestReps();
            return pbReps != null ? weightPart + " x " + pbReps : weightPart;
        }
        // Fallback for existing data without a stored PB
        return workoutExercise.getSets().stream()
            .filter(s -> s.getWeight() != null && s.getWeight().compareTo(BigDecimal.ZERO) > 0)
            .max(java.util.Comparator.comparing(WorkoutSet::getWeight))
            .map(s -> {
                String weightPart = s.getWeight().stripTrailingZeros().toPlainString() + "kg";
                return s.getReps() != null ? weightPart + " x " + s.getReps() : weightPart;
            })
            .orElse("-");
    }

    private String normalizeDay(String day) {
        String trimmed = normalizeRequired(day, "Workout day");
        return VALID_DAYS.stream()
            .filter(validDay -> validDay.equalsIgnoreCase(trimmed))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid workout day"));
    }

    private String resolveColor(UUID ownerId, String requestedColor) {
        if (requestedColor != null && !requestedColor.trim().isEmpty()) {
            return requestedColor.trim();
        }

        int workoutCount = workoutRepository.findAllByOwnerIdOrderByCreatedAtAsc(ownerId).size();
        return DEFAULT_COLORS.get(workoutCount % DEFAULT_COLORS.size());
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private void resequenceExercises(Workout workout) {
        java.util.List<WorkoutExercise> exercises = new java.util.ArrayList<>(workout.getExercises());
        for (int index = 0; index < exercises.size(); index++) {
            exercises.get(index).setPosition(index + 1);
        }
    }

    private void resequenceSets(WorkoutExercise workoutExercise) {
        List<WorkoutSet> sets = new ArrayList<>(workoutExercise.getSets());
        for (int index = 0; index < sets.size(); index++) {
            sets.get(index).setPosition(index + 1);
        }
    }

    private void rollOverWorkoutsIfNeeded(List<Workout> workouts) {
        int currentWeekMarker = getCurrentWeekMarker();
        List<WorkoutSet> changedSets = new ArrayList<>();

        for (Workout workout : workouts) {
            collectSetsThatNeedWeeklyRollover(workout, currentWeekMarker, changedSets);
        }

        if (!changedSets.isEmpty()) {
            workoutSetRepository.saveAll(changedSets);
        }
    }

    private void rollOverWorkoutSetsIfNeeded(Workout workout) {
        int currentWeekMarker = getCurrentWeekMarker();
        List<WorkoutSet> changedSets = new ArrayList<>();
        collectSetsThatNeedWeeklyRollover(workout, currentWeekMarker, changedSets);
        if (!changedSets.isEmpty()) {
            workoutSetRepository.saveAll(changedSets);
        }
    }

    private void collectSetsThatNeedWeeklyRollover(Workout workout, int currentWeekMarker, List<WorkoutSet> changedSets) {
        for (WorkoutExercise exercise : workout.getExercises()) {
            for (WorkoutSet workoutSet : exercise.getSets()) {
                Integer marker = workoutSet.getWeekMarker();

                // Existing rows created before weekMarker existed are initialized lazily.
                if (marker == null) {
                    workoutSet.setWeekMarker(currentWeekMarker);
                    changedSets.add(workoutSet);
                    continue;
                }

                if (!marker.equals(currentWeekMarker)) {
                    workoutSet.setLastWeekWeight(workoutSet.getWeight());
                    workoutSet.setLastWeekReps(workoutSet.getReps());
                    workoutSet.setLastWeek(formatLastWeekValue(workoutSet));
                    // Keep weight and reps pre-filled from last week
                    workoutSet.setCompleted(false);
                    workoutSet.setWeekMarker(currentWeekMarker);
                    changedSets.add(workoutSet);
                }
            }
        }
    }

    private String formatLastWeekValue(WorkoutSet workoutSet) {
        String weightValue = workoutSet.getWeight() == null
            ? "-"
            : workoutSet.getWeight().stripTrailingZeros().toPlainString() + "kg";
        String repsValue = workoutSet.getReps() == null ? "-" : workoutSet.getReps().toString();

        if ("-".equals(weightValue) && "-".equals(repsValue)) {
            return "-";
        }

        return weightValue + " x " + repsValue;
    }

    private int getCurrentWeekMarker() {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate now = LocalDate.now();
        int weekBasedYear = now.get(weekFields.weekBasedYear());
        int weekOfYear = now.get(weekFields.weekOfWeekBasedYear());
        return weekBasedYear * 100 + weekOfYear;
    }
}