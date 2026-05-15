import React, { useEffect, useState } from 'react';
import './index.css';

import Header from './components/Header';
import Footer from './components/Footer';
import LoginPage from './components/LoginPage';
import Dashboard from './components/Dashboard';
import EditWorkout from './components/EditWorkout';
import { deleteCurrentUser, getCurrentUser, loginUser, signupUser } from './api/authApi';
import {
  addExerciseToWorkout,
  addSetToWorkoutExercise,
  createWorkout,
  deleteExerciseFromWorkout,
  deleteWorkoutSet,
  getExercises,
  getWorkouts,
  reorderExercisesInWorkout,
  updateWorkoutSet
} from './api/workoutApi';

const COLORS = ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444', '#ec4899', '#14b8a6'];

function App() {
  const [view, setView] = useState('login'); // 'login' | 'dashboard'
  const [workouts, setWorkouts] = useState([]);
  const [selectedWorkoutId, setSelectedWorkoutId] = useState(null);
  const [authLoading, setAuthLoading] = useState(false);
  const [user, setUser] = useState(null);
  const [exerciseCatalog, setExerciseCatalog] = useState([]);

  const getToken = () => localStorage.getItem('token');

  const replaceWorkout = (updatedWorkout) => {
    setWorkouts((currentWorkouts) => currentWorkouts.map((workout) => (
      workout.id === updatedWorkout.id ? updatedWorkout : workout
    )));
  };

  const loadWorkoutData = async (token) => {
    const [loadedWorkouts, loadedExercises] = await Promise.all([
      getWorkouts(token),
      getExercises(token)
    ]);

    setWorkouts(loadedWorkouts);
    setExerciseCatalog(loadedExercises);
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      return;
    }

    getCurrentUser(token)
      .then(async (currentUser) => {
        setUser(currentUser);
        await loadWorkoutData(token);
        setView('dashboard');
      })
      .catch(() => {
        localStorage.removeItem('token');
        setExerciseCatalog([]);
        setWorkouts([]);
      });
  }, []);

  /* ── Auth ── */
  const handleLogin = async (username, password) => {
    setAuthLoading(true);
    try {
      const response = await loginUser({ username, password });
      localStorage.setItem('token', response.token);
      setUser({
        id: response.userId,
        username: response.username,
        name: response.name,
        role: response.role
      });
      await loadWorkoutData(response.token);
      setView('dashboard');
    } finally {
      setAuthLoading(false);
    }
  };

  const handleSignup = async (name, username, password) => {
    setAuthLoading(true);
    try {
      const response = await signupUser({ name, username, password });
      localStorage.setItem('token', response.token);
      setUser({
        id: response.userId,
        username: response.username,
        name: response.name,
        role: response.role
      });
      await loadWorkoutData(response.token);
      setView('dashboard');
    } finally {
      setAuthLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setWorkouts([]);
    setExerciseCatalog([]);
    setSelectedWorkoutId(null);
    setView('login');
  };

  const handleDeleteAccount = async () => {
    if (window.confirm('Are you sure you want to delete your account? All progress will be lost.')) {
      const token = localStorage.getItem('token');
      if (token) {
        await deleteCurrentUser(token);
      }

      localStorage.removeItem('token');
      setWorkouts([]);
      setExerciseCatalog([]);
      setUser(null);
      setSelectedWorkoutId(null);
      setView('login');
    }
  };

  /* ── Workouts ── */
  const handleAddWorkout = async (day, muscle) => {
    const token = getToken();
    const color = COLORS[workouts.length % COLORS.length];
    const workout = await createWorkout(token, { day, muscle, color });
    setWorkouts((currentWorkouts) => [...currentWorkouts, workout]);
  };

  /* ── Exercises ── */
  const handleAddExercise = async (exerciseName) => {
    const token = getToken();
    const updatedWorkout = await addExerciseToWorkout(token, selectedWorkoutId, { exerciseName });
    replaceWorkout(updatedWorkout);
  };

  const handleDeleteExercise = async (workoutId, exerciseId) => {
    const token = getToken();
    const updatedWorkout = await deleteExerciseFromWorkout(token, workoutId, exerciseId);
    replaceWorkout(updatedWorkout);
  };

  const handleReorderExercises = async (workoutId, exerciseIds) => {
    const token = getToken();
    const updatedWorkout = await reorderExercisesInWorkout(token, workoutId, exerciseIds);
    replaceWorkout(updatedWorkout);
  };

  /* ── Sets ── */
  const handleAddSet = async (workoutId, exerciseId) => {
    const token = getToken();
    const updatedWorkout = await addSetToWorkoutExercise(token, workoutId, exerciseId);
    replaceWorkout(updatedWorkout);
  };

  const handleRemoveSet = async (workoutId, exerciseId, setId) => {
    const token = getToken();
    const updatedWorkout = await deleteWorkoutSet(token, workoutId, exerciseId, setId);
    replaceWorkout(updatedWorkout);
  };

  const handleUpdateSet = async (workoutId, exerciseId, setId, field, value) => {
    const token = getToken();
    const normalizedValue = field === 'weight' || field === 'reps'
      ? (value === '' ? null : Number(value))
      : value;
    const updatedWorkout = await updateWorkoutSet(token, workoutId, exerciseId, setId, {
      [field]: normalizedValue
    });
    replaceWorkout(updatedWorkout);
  };

  /* ── Render: Login ── */
  if (view === 'login') {
    return (
      <>
        <LoginPage onLogin={handleLogin} onSignup={handleSignup} loading={authLoading} />
        <Footer />
      </>
    );
  }

  /* ── Render: Edit Workout ── */
  if (selectedWorkoutId !== null) {
    const activeWorkout = workouts.find(w => w.id === selectedWorkoutId);
    if (!activeWorkout) { setSelectedWorkoutId(null); return null; }

    return (
      <div className="dashboard-container">
        <div className="blobs">
          <div className="blob blob-1"></div>
          <div className="blob blob-2 blob-2-alt"></div>
        </div>
        <Header
          title="Workout Details"
          showBack
          onBack={() => setSelectedWorkoutId(null)}
        />
        <EditWorkout
          workout={activeWorkout}
          exerciseCatalog={exerciseCatalog}
          onAddExercise={handleAddExercise}
          onDeleteExercise={handleDeleteExercise}
          onAddSet={handleAddSet}
          onRemoveSet={handleRemoveSet}
          onUpdateSet={handleUpdateSet}
          onReorderExercises={handleReorderExercises}
        />
        <Footer />
      </div>
    );
  }

  /* ── Render: Dashboard ── */
  return (
    <div className="dashboard-container">
      <div className="blobs">
        <div className="blob blob-1"></div>
        <div className="blob blob-2 blob-2-alt"></div>
      </div>
      <Header
        title="Fitness Dashboard"
        userName={user?.name || user?.username}
        onLogout={handleLogout}
        onDeleteAccount={handleDeleteAccount}
      />
      <Dashboard
        workouts={workouts}
        onAddWorkout={handleAddWorkout}
        onSelectWorkout={setSelectedWorkoutId}
      />
      <Footer />
    </div>
  );
}

export default App;
