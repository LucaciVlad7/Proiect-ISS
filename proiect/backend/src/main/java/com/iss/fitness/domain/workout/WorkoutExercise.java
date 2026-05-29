package com.iss.fitness.domain.workout;

import com.iss.fitness.domain.exercise.Exercise;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private int position;

    @Column(name = "personal_best", precision = 7, scale = 2)
    private BigDecimal personalBest;

    @Column(name = "personal_best_reps")
    private Integer personalBestReps;

    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private Set<WorkoutSet> sets = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BigDecimal getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(BigDecimal personalBest) {
        this.personalBest = personalBest;
    }

    public Integer getPersonalBestReps() {
        return personalBestReps;
    }

    public void setPersonalBestReps(Integer personalBestReps) {
        this.personalBestReps = personalBestReps;
    }

    public Set<WorkoutSet> getSets() {
        return sets;
    }
}