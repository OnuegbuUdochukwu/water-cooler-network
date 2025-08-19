import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './CorporateNavigation.css';

const CorporateNavigation: React.FC = () => {
  const location = useLocation();

  const isActive = (path: string) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <div className="corporate-navigation">
      <div className="nav-header">
        <h2>🏢 Corporate Spaces</h2>
        <p>Manage your company's workspace</p>
      </div>
      
      <nav className="nav-menu">
        <Link 
          to="/corporate" 
          className={`nav-item ${isActive('/corporate') && location.pathname === '/corporate' ? 'active' : ''}`}
        >
          <span className="nav-icon">📊</span>
          <span className="nav-label">Dashboard</span>
        </Link>
        
        <Link 
          to="/corporate/departments" 
          className={`nav-item ${isActive('/corporate/departments') ? 'active' : ''}`}
        >
          <span className="nav-icon">🏛️</span>
          <span className="nav-label">Departments</span>
        </Link>
        
        <Link 
          to="/corporate/invitations" 
          className={`nav-item ${isActive('/corporate/invitations') ? 'active' : ''}`}
        >
          <span className="nav-icon">✉️</span>
          <span className="nav-label">Invitations</span>
        </Link>
        
        <Link 
          to="/corporate/announcements" 
          className={`nav-item ${isActive('/corporate/announcements') ? 'active' : ''}`}
        >
          <span className="nav-icon">📢</span>
          <span className="nav-label">Announcements</span>
        </Link>
        
        <Link 
          to="/corporate/employees" 
          className={`nav-item ${isActive('/corporate/employees') ? 'active' : ''}`}
        >
          <span className="nav-icon">👥</span>
          <span className="nav-label">Employees</span>
        </Link>
        
                <Link 
          to="/corporate/subscriptions"
          className={`nav-item ${isActive('/corporate/subscriptions') ? 'active' : ''}`}
        >
          <span className="nav-icon">💳</span>
          <span className="nav-label">Subscriptions</span>
        </Link>
        
        <Link 
          to="/analytics"
          className={`nav-item ${isActive('/analytics') ? 'active' : ''}`}
        >
          <span className="nav-icon">📈</span>
          <span className="nav-label">Analytics</span>
        </Link>
        
        <Link 
          to="/corporate/settings"
          className={`nav-item ${isActive('/corporate/settings') ? 'active' : ''}`}
        >
          <span className="nav-icon">⚙️</span>
          <span className="nav-label">Settings</span>
        </Link>
      </nav>
    </div>
  );
};

export default CorporateNavigation;
