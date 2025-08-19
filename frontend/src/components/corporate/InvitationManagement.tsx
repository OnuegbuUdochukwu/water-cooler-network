import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './InvitationManagement.css';

interface CompanyInvitation {
  id: number;
  email: string;
  status: 'PENDING' | 'ACCEPTED' | 'EXPIRED' | 'CANCELLED';
  departmentId?: number;
  departmentName?: string;
  invitedByUserName?: string;
  createdAt: string;
  expiresAt: string;
  acceptedAt?: string;
}

interface Department {
  id: number;
  name: string;
  description: string;
}

const InvitationManagement: React.FC = () => {
  const [invitations, setInvitations] = useState<CompanyInvitation[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>('all');
  const [companyId, setCompanyId] = useState<number | null>(null);

  const [inviteForm, setInviteForm] = useState({
    email: '',
    departmentId: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      // Get user's company info
      const userResponse = await axios.get('/api/users/profile', { headers });
      const userCompanyId = userResponse.data.companyId;
      
      if (!userCompanyId) {
        setError('You are not associated with any company');
        return;
      }

      setCompanyId(userCompanyId);

      // Fetch invitations
      const invitationsResponse = await axios.get(`/api/company-invitations/company/${userCompanyId}`, { headers });
      setInvitations(invitationsResponse.data);

      // Fetch departments
      const departmentsResponse = await axios.get(`/api/departments/company/${userCompanyId}`, { headers });
      setDepartments(departmentsResponse.data);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load invitations');
    } finally {
      setLoading(false);
    }
  };

  const handleSendInvitation = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.post('/api/company-invitations', {
        companyId,
        email: inviteForm.email,
        departmentId: inviteForm.departmentId ? parseInt(inviteForm.departmentId) : undefined,
        invitedByUserId: 1 // This should come from current user context
      }, { headers });

      setShowInviteModal(false);
      setInviteForm({ email: '', departmentId: '' });
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to send invitation');
    }
  };

  const handleCancelInvitation = async (invitationId: number) => {
    if (!window.confirm('Are you sure you want to cancel this invitation?')) return;

    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.post(`/api/company-invitations/${invitationId}/cancel`, {}, { headers });
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to cancel invitation');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'pending';
      case 'ACCEPTED': return 'accepted';
      case 'EXPIRED': return 'expired';
      case 'CANCELLED': return 'cancelled';
      default: return 'pending';
    }
  };

  const filteredInvitations = invitations.filter(invitation => {
    if (filterStatus === 'all') return true;
    return invitation.status === filterStatus.toUpperCase();
  });

  if (loading) {
    return (
      <div className="invitation-management loading">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading invitations...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="invitation-management error">
        <div className="error-message">
          <h3>Error</h3>
          <p>{error}</p>
          <button onClick={fetchData} className="retry-button">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="invitation-management">
      <CorporateNavigation />
      <div className="management-header">
        <div className="header-content">
          <h1>Invitation Management</h1>
          <div className="header-stats">
            <span className="stat">
              Total: {invitations.length}
            </span>
            <span className="stat pending">
              Pending: {invitations.filter(inv => inv.status === 'PENDING').length}
            </span>
            <span className="stat accepted">
              Accepted: {invitations.filter(inv => inv.status === 'ACCEPTED').length}
            </span>
          </div>
        </div>
        <button 
          className="invite-button"
          onClick={() => setShowInviteModal(true)}
        >
          Send Invitation
        </button>
      </div>

      <div className="filters-section">
        <div className="filter-group">
          <label>Filter by Status:</label>
          <select 
            value={filterStatus} 
            onChange={(e) => setFilterStatus(e.target.value)}
          >
            <option value="all">All Invitations</option>
            <option value="pending">Pending</option>
            <option value="accepted">Accepted</option>
            <option value="expired">Expired</option>
            <option value="cancelled">Cancelled</option>
          </select>
        </div>
      </div>

      <div className="invitations-list">
        {filteredInvitations.map(invitation => (
          <div key={invitation.id} className="invitation-card">
            <div className="invitation-header">
              <div className="invitation-email">{invitation.email}</div>
              <span className={`status ${getStatusColor(invitation.status)}`}>
                {invitation.status}
              </span>
            </div>
            
            <div className="invitation-details">
              {invitation.departmentName && (
                <div className="detail-item">
                  <span className="label">Department:</span>
                  <span className="value">{invitation.departmentName}</span>
                </div>
              )}
              <div className="detail-item">
                <span className="label">Invited by:</span>
                <span className="value">{invitation.invitedByUserName || 'Unknown'}</span>
              </div>
              <div className="detail-item">
                <span className="label">Sent:</span>
                <span className="value">{new Date(invitation.createdAt).toLocaleDateString()}</span>
              </div>
              <div className="detail-item">
                <span className="label">Expires:</span>
                <span className="value">{new Date(invitation.expiresAt).toLocaleDateString()}</span>
              </div>
              {invitation.acceptedAt && (
                <div className="detail-item">
                  <span className="label">Accepted:</span>
                  <span className="value">{new Date(invitation.acceptedAt).toLocaleDateString()}</span>
                </div>
              )}
            </div>
            
            <div className="invitation-actions">
              {invitation.status === 'PENDING' && (
                <button 
                  className="action-button cancel"
                  onClick={() => handleCancelInvitation(invitation.id)}
                >
                  Cancel
                </button>
              )}
              <button className="action-button resend" disabled>
                Resend
              </button>
            </div>
          </div>
        ))}
      </div>

      {filteredInvitations.length === 0 && (
        <div className="no-invitations">
          <h3>No Invitations Found</h3>
          <p>
            {filterStatus === 'all' 
              ? 'No invitations have been sent yet.' 
              : `No ${filterStatus} invitations found.`
            }
          </p>
          <button 
            className="invite-button"
            onClick={() => setShowInviteModal(true)}
          >
            Send First Invitation
          </button>
        </div>
      )}

      {/* Invite Modal */}
      {showInviteModal && (
        <div className="modal-overlay" onClick={() => setShowInviteModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Send Invitation</h2>
              <button 
                className="close-button" 
                onClick={() => setShowInviteModal(false)}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleSendInvitation}>
              <div className="form-group">
                <label htmlFor="email">Email Address *</label>
                <input
                  type="email"
                  id="email"
                  value={inviteForm.email}
                  onChange={e => setInviteForm(prev => ({ ...prev, email: e.target.value }))}
                  required
                  placeholder="Enter email address"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="departmentId">Department (Optional)</label>
                <select
                  id="departmentId"
                  value={inviteForm.departmentId}
                  onChange={e => setInviteForm(prev => ({ ...prev, departmentId: e.target.value }))}
                >
                  <option value="">No specific department</option>
                  {departments.map(dept => (
                    <option key={dept.id} value={dept.id}>
                      {dept.name}
                    </option>
                  ))}
                </select>
              </div>
              
              <div className="form-actions">
                <button 
                  type="button" 
                  className="cancel-button" 
                  onClick={() => setShowInviteModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="submit-button">
                  Send Invitation
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default InvitationManagement;
