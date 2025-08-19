import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './CompanyDashboard.css';

interface Company {
  id: number;
  name: string;
  adminId: number;
  adminName?: string;
  subscriptionTier: string;
  isActive: boolean;
  createdAt: string;
  employeeCount?: number;
  departmentCount?: number;
}

interface Department {
  id: number;
  name: string;
  description: string;
  companyId: number;
  headUserId?: number;
  headUserName?: string;
  memberCount?: number;
  isActive: boolean;
}

interface CompanyInvitation {
  id: number;
  email: string;
  status: string;
  departmentName?: string;
  invitedByUserName?: string;
  createdAt: string;
  expiresAt: string;
}

const CompanyDashboard: React.FC = () => {
  const [company, setCompany] = useState<Company | null>(null);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [invitations, setInvitations] = useState<CompanyInvitation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'overview' | 'departments' | 'invitations' | 'settings'>('overview');

  useEffect(() => {
    fetchCompanyData();
  }, []);

  const fetchCompanyData = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      // Get user's company info
      const userResponse = await axios.get('/api/users/profile', { headers });
      const companyId = userResponse.data.companyId;

      if (!companyId) {
        setError('You are not associated with any company');
        return;
      }

      // Fetch company details
      const companyResponse = await axios.get(`/api/companies/${companyId}`, { headers });
      setCompany(companyResponse.data);

      // Fetch departments
      const departmentsResponse = await axios.get(`/api/departments/company/${companyId}`, { headers });
      setDepartments(departmentsResponse.data);

      // Fetch invitations (if user is admin)
      try {
        const invitationsResponse = await axios.get(`/api/company-invitations/company/${companyId}`, { headers });
        setInvitations(invitationsResponse.data);
      } catch (invError) {
        // User might not have admin privileges
        console.log('Could not fetch invitations:', invError);
      }

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load company data');
    } finally {
      setLoading(false);
    }
  };


  if (loading) {
    return (
      <div className="company-dashboard loading">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading company dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="company-dashboard error">
        <div className="error-message">
          <h3>Error</h3>
          <p>{error}</p>
          <button onClick={fetchCompanyData} className="retry-button">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  if (!company) {
    return (
      <div className="company-dashboard">
        <div className="no-company">
          <h3>No Company Association</h3>
          <p>You are not currently associated with any company.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="company-dashboard">
      <CorporateNavigation />
      <div className="dashboard-header">
        <div className="company-info">
          <h1>{company.name}</h1>
          <div className="company-meta">
            <span className={`subscription-tier ${company.subscriptionTier.toLowerCase()}`}>
              {company.subscriptionTier}
            </span>
            <span className="employee-count">
              {company.employeeCount || 0} employees
            </span>
            <span className="department-count">
              {company.departmentCount || departments.length} departments
            </span>
          </div>
        </div>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={`tab ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Overview
        </button>
        <button 
          className={`tab ${activeTab === 'departments' ? 'active' : ''}`}
          onClick={() => setActiveTab('departments')}
        >
          Departments ({departments.length})
        </button>
        <button 
          className={`tab ${activeTab === 'invitations' ? 'active' : ''}`}
          onClick={() => setActiveTab('invitations')}
        >
          Invitations ({invitations.length})
        </button>
        <button 
          className={`tab ${activeTab === 'settings' ? 'active' : ''}`}
          onClick={() => setActiveTab('settings')}
        >
          Settings
        </button>
      </div>

      <div className="dashboard-content">
        {activeTab === 'overview' && (
          <div className="overview-tab">
            <div className="stats-grid">
              <div className="stat-card">
                <h3>Total Employees</h3>
                <div className="stat-number">{company.employeeCount || 0}</div>
              </div>
              <div className="stat-card">
                <h3>Departments</h3>
                <div className="stat-number">{departments.length}</div>
              </div>
              <div className="stat-card">
                <h3>Pending Invitations</h3>
                <div className="stat-number">
                  {invitations.filter(inv => inv.status === 'PENDING').length}
                </div>
              </div>
              <div className="stat-card">
                <h3>Company Since</h3>
                <div className="stat-text">
                  {new Date(company.createdAt).toLocaleDateString()}
                </div>
              </div>
            </div>

            <div className="recent-activity">
              <h3>Recent Departments</h3>
              <div className="department-list">
                {departments.slice(0, 5).map(dept => (
                  <div key={dept.id} className="department-item">
                    <div className="department-info">
                      <h4>{dept.name}</h4>
                      <p>{dept.description}</p>
                    </div>
                    <div className="department-meta">
                      <span className="member-count">{dept.memberCount || 0} members</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'departments' && (
          <div className="departments-tab">
            <div className="tab-header">
              <h3>Company Departments</h3>
              <button className="create-button">Create Department</button>
            </div>
            <div className="departments-grid">
              {departments.map(dept => (
                <div key={dept.id} className="department-card">
                  <div className="department-header">
                    <h4>{dept.name}</h4>
                    <span className={`status ${dept.isActive ? 'active' : 'inactive'}`}>
                      {dept.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </div>
                  <p className="department-description">{dept.description}</p>
                  <div className="department-stats">
                    <span className="member-count">{dept.memberCount || 0} members</span>
                    {dept.headUserName && (
                      <span className="department-head">Head: {dept.headUserName}</span>
                    )}
                  </div>
                  <div className="department-actions">
                    <button className="action-button">View Members</button>
                    <button className="action-button">Edit</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'invitations' && (
          <div className="invitations-tab">
            <div className="tab-header">
              <h3>Company Invitations</h3>
              <button className="create-button">Send Invitation</button>
            </div>
            <div className="invitations-list">
              {invitations.map(invitation => (
                <div key={invitation.id} className="invitation-item">
                  <div className="invitation-info">
                    <div className="invitation-email">{invitation.email}</div>
                    <div className="invitation-meta">
                      {invitation.departmentName && (
                        <span className="department">Department: {invitation.departmentName}</span>
                      )}
                      <span className="invited-by">
                        Invited by: {invitation.invitedByUserName || 'Unknown'}
                      </span>
                      <span className="created-date">
                        {new Date(invitation.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                  <div className="invitation-status">
                    <span className={`status ${invitation.status.toLowerCase()}`}>
                      {invitation.status}
                    </span>
                    {invitation.status === 'PENDING' && (
                      <button className="cancel-button">Cancel</button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'settings' && (
          <div className="settings-tab">
            <div className="settings-section">
              <h3>Company Settings</h3>
              <div className="setting-item">
                <label>Company Name</label>
                <input type="text" value={company.name} readOnly />
              </div>
              <div className="setting-item">
                <label>Subscription Tier</label>
                <select value={company.subscriptionTier} disabled>
                  <option value="FREE">Free</option>
                  <option value="BASIC">Basic</option>
                  <option value="PREMIUM">Premium</option>
                  <option value="ENTERPRISE">Enterprise</option>
                </select>
              </div>
              <div className="setting-item">
                <label>Company Status</label>
                <span className={`status ${company.isActive ? 'active' : 'inactive'}`}>
                  {company.isActive ? 'Active' : 'Inactive'}
                </span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CompanyDashboard;
