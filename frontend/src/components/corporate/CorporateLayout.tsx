import React from 'react';
import { Outlet } from 'react-router-dom';
import CorporateNavigation from './CorporateNavigation';
import './CorporateLayout.css';

const CorporateLayout: React.FC = () => {
  return (
    <div className="corporate-layout">
      <CorporateNavigation />
      <div className="corporate-content">
        <Outlet />
      </div>
    </div>
  );
};

export default CorporateLayout;
