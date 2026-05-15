import React, { useState } from 'react';
import ExerciseCard from './ExerciseCard';

function EditWorkout({ 
  workout, 
  exerciseCatalog,
  onAddExercise, 
  onDeleteExercise, 
  onAddSet, 
  onRemoveSet, 
  onUpdateSet,
  onReorderExercises
}) {
  const [exerciseName, setExerciseName] = useState('');
  const [draggedIdx, setDraggedIdx] = useState(null);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!exerciseName.trim()) return;
    onAddExercise(exerciseName.trim());
    setExerciseName('');
  };

  /* Drag-and-drop handlers (kept local to this component) */
  const handleDragStart = (e, index) => {
    setDraggedIdx(index);
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', index);
    setTimeout(() => { e.target.classList.add('dragging'); }, 0);
  };

  const handleDragEnter = (e, targetIndex) => {
    e.preventDefault();
    if (draggedIdx === null || draggedIdx === targetIndex) return;

    // Reorder exercises locally and trigger API call
    const newExercises = Array.from(workout.exercises);
    const [draggedExercise] = newExercises.splice(draggedIdx, 1);
    newExercises.splice(targetIndex, 0, draggedExercise);

    const reorderedIds = newExercises.map(ex => ex.id);
    onReorderExercises(workout.id, reorderedIds);
    setDraggedIdx(targetIndex);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  };

  const handleDragEnd = (e) => {
    setDraggedIdx(null);
    e.target.classList.remove('dragging');
  };

  return (
    <main className="dashboard-content detail-content">
      <div className="glass-panel detail-panel" style={{ '--accent-color': workout.color }}>
        <div className="widget-accent detail-accent"></div>
        
        <div className="detail-title-block">
          <span className="widget-day detail-day">{workout.day}</span>
          <h3 className="detail-muscle">{workout.muscle}</h3>
        </div>

        <div className="exercises-section">
          <h4 className="exercises-heading">Exercises</h4>
          
          {workout.exercises.length === 0 ? (
            <div className="empty-state exercises-empty">
              <p>No exercises listed yet. Add one below!</p>
            </div>
          ) : (
            <ul className="exercise-list">
              {workout.exercises.map((ex, index) => (
                <ExerciseCard
                  key={ex.id}
                  exercise={ex}
                  index={index}
                  workoutId={workout.id}
                  isDragTarget={draggedIdx === index}
                  onDelete={onDeleteExercise}
                  onAddSet={onAddSet}
                  onRemoveSet={onRemoveSet}
                  onUpdateSet={onUpdateSet}
                  onDragStart={handleDragStart}
                  onDragEnter={handleDragEnter}
                  onDragOver={handleDragOver}
                  onDragEnd={handleDragEnd}
                />
              ))}
            </ul>
          )}
        </div>

        <div className="add-exercise-section">
          <h4 className="add-exercise-heading">Add New Exercise</h4>
          <form className="add-workout-form" onSubmit={handleSubmit}>
            <div className="input-group input-flex-grow">
              <label>Exercise Name</label>
              <input
                type="text"
                value={exerciseName}
                onChange={(e) => setExerciseName(e.target.value)}
                placeholder="e.g. Bench Press"
                required
                minLength={2}
                maxLength={120}
              />
            </div>
            <div className="input-group input-flex-auto">
              <button
                type="submit"
                className="btn btn-primary btn-add-exercise"
                disabled={!exerciseName.trim()}
              >
                Add Exercise
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}

export default EditWorkout;
