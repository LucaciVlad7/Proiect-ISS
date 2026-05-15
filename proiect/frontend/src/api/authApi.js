const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
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

export function signupUser(payload) {
  return request('/api/auth/signup', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function loginUser(payload) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function getCurrentUser(token) {
  return request('/api/users/me', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
}

export function deleteCurrentUser(token) {
  return request('/api/users/me', {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
}
