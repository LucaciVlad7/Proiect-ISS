import React from 'react';

function Header({ title, onBack, onLogout, onDeleteAccount, showBack, userName }) {
  return (
    <header className="dashboard-header glass-panel">
      {showBack ? (
        <div className="back-header">
          <button className="btn btn-secondary btn-sm btn-back" onClick={onBack}>
            ←
          </button>
          <h2>{title}</h2>
        </div>
      ) : (
        <h2>{title}</h2>
      )}
      <div className="header-actions">
        <span>Welcome, <strong>{userName || 'athlete'}</strong>!</span>
        {onDeleteAccount && (
          <button className="btn btn-secondary btn-sm btn-danger" onClick={onDeleteAccount}>
            Delete Account
          </button>
        )}
        {onLogout && (
          <button className="btn btn-secondary btn-sm btn-header" onClick={onLogout}>
            Log Out
          </button>
        )}
      </div>
    </header>
  );
}

export default Header;
