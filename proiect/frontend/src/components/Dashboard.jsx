import React, { useState } from 'react';
import WorkoutCard from './WorkoutCard';

function Dashboard({ workouts, onAddWorkout, onSelectWorkout }) {
  const [showAddForm, setShowAddForm] = useState(false);
  const [newWorkout, setNewWorkout] = useState({ day: 'Monday', muscle: '' });

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!newWorkout.muscle.trim()) return;
    onAddWorkout(newWorkout.day, newWorkout.muscle);
    setNewWorkout({ day: 'Monday', muscle: '' });
    setShowAddForm(false);
  };

  return (
    <main className="dashboard-content">
      <div className="content-header">
        <div>
          <h3>Your Weekly Schedule</h3>
          <p>Workouts are saved in your account and loaded again after refresh.</p>
        </div>
        {!showAddForm && (
          <button className="btn btn-primary btn-sm btn-add-workout" onClick={() => setShowAddForm(true)}>
            + Add Workout
          </button>
        )}
      </div>

      {showAddForm && (
        <div className="add-workout-card glass-panel">
          <h4 className="card-form-title">Create New Workout</h4>
          <form className="add-workout-form" onSubmit={handleSubmit}>
            <div className="input-group">
              <label>Day of the Week</label>
              <select 
                value={newWorkout.day} 
                onChange={(e) => setNewWorkout({...newWorkout, day: e.target.value})}
              >
                <option value="Monday">Monday</option>
                <option value="Tuesday">Tuesday</option>
                <option value="Wednesday">Wednesday</option>
                <option value="Thursday">Thursday</option>
                <option value="Friday">Friday</option>
                <option value="Saturday">Saturday</option>
                <option value="Sunday">Sunday</option>
              </select>
            </div>
            <div className="input-group">
              <label>Muscle Group</label>
              <input 
                type="text" 
                placeholder="e.g. Chest & Triceps" 
                value={newWorkout.muscle}
                onChange={(e) => setNewWorkout({...newWorkout, muscle: e.target.value})}
                required
              />
            </div>
            <div className="input-group form-actions-row">
              <button type="button" className="btn btn-secondary btn-auto" onClick={() => setShowAddForm(false)}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary btn-auto">
                Save
              </button>
            </div>
          </form>
        </div>
      )}

      {workouts.length === 0 ? (
        <div className="empty-state glass-panel">
          <svg className="empty-icon" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.2)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
          </svg>
          <h3>No Workouts Found</h3>
          <p>Your weekly schedule is empty. Click "+ Add Workout" to create one!</p>
        </div>
      ) : (
        <div className="widgets-grid">
          {workouts.map((workout) => (
            <WorkoutCard key={workout.id} workout={workout} onSelect={onSelectWorkout} />
          ))}
        </div>
      )}
    </main>
  );
}

export default Dashboard;
