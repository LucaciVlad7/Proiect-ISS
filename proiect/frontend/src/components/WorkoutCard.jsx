import React from 'react';

function WorkoutCard({ workout, onSelect }) {
  return (
    <div className="widget-card glass-panel" style={{ '--accent-color': workout.color }}>
      <div className="widget-accent"></div>
      <div className="widget-content">
        <span className="widget-day">{workout.day}</span>
        <h4 className="widget-muscle">{workout.muscle}</h4>
        <span className="widget-exercise-count">
          {workout.exercises ? workout.exercises.length : 0} Exercises
        </span>
      </div>
      <button className="widget-action-btn" onClick={() => onSelect(workout.id)}>
        View &amp; Edit
      </button>
    </div>
  );
}

export default WorkoutCard;
