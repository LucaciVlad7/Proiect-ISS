const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function request(path, token, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      ...(options.headers || {})
    },
    ...options
  });

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.message || 'Request failed');
  }

  return data;
}

export function getExercises(token) {
  return request('/api/exercises', token, { method: 'GET' });
}

export function getWorkouts(token) {
  return request('/api/workouts', token, { method: 'GET' });
}

export function createWorkout(token, payload) {
  return request('/api/workouts', token, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function addExerciseToWorkout(token, workoutId, payload) {
  return request(`/api/workouts/${workoutId}/exercises`, token, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function deleteExerciseFromWorkout(token, workoutId, workoutExerciseId) {
  return request(`/api/workouts/${workoutId}/exercises/${workoutExerciseId}`, token, {
    method: 'DELETE'
  });
}

export function reorderExercisesInWorkout(token, workoutId, exerciseIds) {
  return request(`/api/workouts/${workoutId}/exercises/reorder`, token, {
    method: 'POST',
    body: JSON.stringify({ exerciseIds })
  });
}

export function addSetToWorkoutExercise(token, workoutId, workoutExerciseId) {
  return request(`/api/workouts/${workoutId}/exercises/${workoutExerciseId}/sets`, token, {
    method: 'POST'
  });
}

export function updateWorkoutSet(token, workoutId, workoutExerciseId, setId, payload) {
  return request(`/api/workouts/${workoutId}/exercises/${workoutExerciseId}/sets/${setId}`, token, {
    method: 'PATCH',
    body: JSON.stringify(payload)
  });
}

export function deleteWorkoutSet(token, workoutId, workoutExerciseId, setId) {
  return request(`/api/workouts/${workoutId}/exercises/${workoutExerciseId}/sets/${setId}`, token, {
    method: 'DELETE'
  });
}