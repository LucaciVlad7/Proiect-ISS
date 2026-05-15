import React, { useState } from 'react';

function LoginPage({ onLogin, onSignup, loading }) {
  const [mode, setMode] = useState('login');
  const [isHovered, setIsHovered] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    const username = e.target.username.value;
    const password = e.target.password.value;
    setError('');

    try {
      if (mode === 'login') {
        await onLogin(username, password);
      } else {
        const name = e.target.name.value;
        await onSignup(name, username, password);
      }
    } catch (err) {
      setError(err.message || 'Authentication failed.');
    }
  };

  return (
    <div className="login-container">
      <div className="blobs">
        <div className="blob blob-1"></div>
        <div className="blob blob-2"></div>
      </div>
      
      <div className="glass-card">
        <div className="card-header">
          <h2>{mode === 'login' ? 'Welcome Back' : 'Create Account'}</h2>
          <p>
            {mode === 'login' 
              ? 'Enter your details to access your account' 
              : 'Sign up to get started'}
          </p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          
          {mode === 'signup' && (
            <div className="input-group fade-in">
              <label htmlFor="name">Full Name</label>
              <input type="text" id="name" placeholder="Enter your name" required />
            </div>
          )}

          <div className="input-group">
            <label htmlFor="username">Username</label>
            <input type="text" id="username" placeholder="Enter your username" required />
          </div>

          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input type="password" id="password" placeholder="••••••••" required />
          </div>

          {error && <p className="auth-error">{error}</p>}

          <div className="actions">
            <button 
              className={`btn btn-primary ${isHovered ? 'hovered' : ''}`}
              type="submit"
              disabled={loading}
              onMouseEnter={() => setIsHovered(true)}
              onMouseLeave={() => setIsHovered(false)}
            >
              {loading ? 'Please wait...' : (mode === 'login' ? 'Sign In' : 'Sign Up')}
            </button>
            
            <div className="divider"><span>OR</span></div>

            <button 
              className="btn btn-secondary" 
              type="button"
              disabled={loading}
              onClick={() => {
                setMode(mode === 'login' ? 'signup' : 'login');
                setIsHovered(false);
                setError('');
              }}
            >
              {mode === 'login' ? 'Create an Account' : 'Back to Login'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
