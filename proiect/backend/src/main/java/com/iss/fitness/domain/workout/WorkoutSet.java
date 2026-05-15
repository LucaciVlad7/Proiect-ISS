package com.iss.fitness.domain.workout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "workout_sets")
public class WorkoutSet {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    private WorkoutExercise workoutExercise;

    @Column(nullable = false)
    private int position;

    private Integer reps;

    @Column(precision = 7, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false, length = 80)
    private String lastWeek;

    @Column(name = "week_marker")
    private Integer weekMarker;

    @Column(nullable = false)
    private boolean completed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WorkoutExercise getWorkoutExercise() {
        return workoutExercise;
    }

    public void setWorkoutExercise(WorkoutExercise workoutExercise) {
        this.workoutExercise = workoutExercise;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(String lastWeek) {
        this.lastWeek = lastWeek;
    }

    public Integer getWeekMarker() {
        return weekMarker;
    }

    public void setWeekMarker(Integer weekMarker) {
        this.weekMarker = weekMarker;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}