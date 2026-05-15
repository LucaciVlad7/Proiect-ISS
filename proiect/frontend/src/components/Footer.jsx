import React from 'react';

function Footer() {
  return (
    <footer className="app-footer">
      <span className="footer-brand">Fitness App</span>
      <span className="footer-copy">© {new Date().getFullYear()} All rights reserved.</span>
    </footer>
  );
}

export default Footer;
