import React from 'react';
import SetRow from './SetRow';

function ExerciseCard({ 
  exercise, 
  index, 
  workoutId, 
  isDragTarget,
  onDelete, 
  onAddSet, 
  onRemoveSet, 
  onUpdateSet,
  onDragStart,
  onDragEnter,
  onDragOver,
  onDragEnd
}) {
  return (
    <li 
      className={`exercise-item complex-item ${isDragTarget ? 'drag-target' : ''}`}
      draggable
      onDragStart={(e) => onDragStart(e, index)}
      onDragEnter={(e) => onDragEnter(e, index)}
      onDragOver={onDragOver}
      onDragEnd={onDragEnd}
    >
      <div className="exercise-header">
        <div className="drag-handle">☰</div>
        <div className="ex-info">
          <span className="ex-name">{exercise.name}</span>
          <div className="ex-stats">
            <span className="stat-pill">PB: <strong>{exercise.pb}</strong></span>
          </div>
        </div>
        <button 
          className="delete-btn" 
          onClick={() => onDelete(workoutId, exercise.id)}
          title="Remove Exercise"
        >
          ✕
        </button>
      </div>

      <div className="sets-container">
        <div className="sets-header">
          <span className="col-set">Set</span>
          <span className="col-last-wk">Last Wk</span>
          <span className="col-flex">kg Data</span>
          <span className="col-flex">Reps</span>
          <span className="col-check">✓</span>
        </div>
        {exercise.sets.map((set, setIdx) => (
          <SetRow 
            key={set.id}
            set={set}
            index={setIdx}
            onUpdate={(setId, field, value) => onUpdateSet(workoutId, exercise.id, setId, field, value)}
            onRemove={(setId) => onRemoveSet(workoutId, exercise.id, setId)}
          />
        ))}
        <button 
          className="add-set-btn" 
          onClick={() => onAddSet(workoutId, exercise.id)}
        >
          + Add Set
        </button>
      </div>
    </li>
  );
}

export default ExerciseCard;
