import React from 'react';

function SetRow({ set, index, onUpdate, onRemove }) {
  const kgPlaceholder = set.lastWeekWeight != null ? `${set.lastWeekWeight} kg` : 'kg';
  const repsPlaceholder = set.lastWeekReps != null ? `${set.lastWeekReps} reps` : 'reps';

  return (
    <div className={`set-row ${set.completed ? 'completed' : ''}`}>
      <span className="set-number">{index + 1}</span>
      <span className="last-wk-value">{set.lastWeek || '-'}</span>
      <input 
        type="number" 
        className="set-input" 
        placeholder={kgPlaceholder}
        value={set.weight}
        onChange={(e) => onUpdate(set.id, 'weight', e.target.value)}
        disabled={set.completed}
      />
      <input 
        type="number" 
        className="set-input" 
        placeholder={repsPlaceholder}
        value={set.reps}
        onChange={(e) => onUpdate(set.id, 'reps', e.target.value)}
        disabled={set.completed}
      />
      <button 
        className={`check-btn ${set.completed ? 'checked' : ''}`}
        onClick={() => onUpdate(set.id, 'completed', !set.completed)}
      >
        ✓
      </button>
      <button 
        className="delete-set-btn"
        onClick={() => onRemove(set.id)}
        disabled={set.completed}
      >
        ✕
      </button>
    </div>
  );
}

export default SetRow;
