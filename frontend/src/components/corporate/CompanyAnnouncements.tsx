import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './CompanyAnnouncements.css';

interface CompanyAnnouncement {
  id: number;
  title: string;
  content: string;
  type: 'GENERAL' | 'URGENT' | 'EVENT' | 'POLICY';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  companyId: number;
  authorId: number;
  authorName?: string;
  targetDepartmentIds?: number[];
  targetDepartmentNames?: string[];
  isPinned: boolean;
  isPublished: boolean;
  publishedAt?: string;
  createdAt: string;
  updatedAt: string;
}

interface Department {
  id: number;
  name: string;
}

const CompanyAnnouncements: React.FC = () => {
  const [announcements, setAnnouncements] = useState<CompanyAnnouncement[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingAnnouncement, setEditingAnnouncement] = useState<CompanyAnnouncement | null>(null);
  const [filterType, setFilterType] = useState<string>('all');
  const [filterPriority, setFilterPriority] = useState<string>('all');
  const [companyId, setCompanyId] = useState<number | null>(null);

  const [formData, setFormData] = useState<{
    title: string;
    content: string;
    type: 'GENERAL' | 'URGENT' | 'EVENT' | 'POLICY';
    priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
    targetDepartmentIds: number[];
    isPinned: boolean;
    isPublished: boolean;
  }>({
    title: '',
    content: '',
    type: 'GENERAL',
    priority: 'MEDIUM',
    targetDepartmentIds: [],
    isPinned: false,
    isPublished: false
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

      // Fetch announcements
      const announcementsResponse = await axios.get(`/api/company-announcements/company/${userCompanyId}`, { headers });
      setAnnouncements(announcementsResponse.data);

      // Fetch departments
      const departmentsResponse = await axios.get(`/api/departments/company/${userCompanyId}`, { headers });
      setDepartments(departmentsResponse.data);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load announcements');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAnnouncement = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.post('/api/company-announcements', {
        ...formData,
        companyId,
        authorId: 1, // This should come from current user context
        targetDepartmentIds: formData.targetDepartmentIds.length > 0 ? formData.targetDepartmentIds : null
      }, { headers });

      closeModal();
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create announcement');
    }
  };

  const handleUpdateAnnouncement = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingAnnouncement) return;

    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.put(`/api/company-announcements/${editingAnnouncement.id}`, {
        title: formData.title,
        content: formData.content,
        type: formData.type,
        priority: formData.priority,
        targetDepartmentIds: formData.targetDepartmentIds.length > 0 ? formData.targetDepartmentIds : null,
        isPinned: formData.isPinned,
        isPublished: formData.isPublished
      }, { headers });

      closeModal();
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update announcement');
    }
  };

  const handleDeleteAnnouncement = async (announcementId: number) => {
    if (!window.confirm('Are you sure you want to delete this announcement?')) return;

    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.delete(`/api/company-announcements/${announcementId}`, { headers });
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete announcement');
    }
  };

  const handleTogglePin = async (announcementId: number, currentPinned: boolean) => {
    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.patch(`/api/company-announcements/${announcementId}/pin`, {
        isPinned: !currentPinned
      }, { headers });

      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update pin status');
    }
  };

  const openEditModal = (announcement: CompanyAnnouncement) => {
    setEditingAnnouncement(announcement);
    setFormData({
      title: announcement.title,
      content: announcement.content,
      type: announcement.type,
      priority: announcement.priority,
      targetDepartmentIds: announcement.targetDepartmentIds || [],
      isPinned: announcement.isPinned,
      isPublished: announcement.isPublished
    });
  };

  const closeModal = () => {
    setShowCreateModal(false);
    setEditingAnnouncement(null);
    setFormData({
      title: '',
      content: '',
      type: 'GENERAL',
      priority: 'MEDIUM',
      targetDepartmentIds: [],
      isPinned: false,
      isPublished: false
    });
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'CRITICAL': return 'critical';
      case 'HIGH': return 'high';
      case 'MEDIUM': return 'medium';
      case 'LOW': return 'low';
      default: return 'medium';
    }
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'URGENT': return 'urgent';
      case 'EVENT': return 'event';
      case 'POLICY': return 'policy';
      case 'GENERAL': return 'general';
      default: return 'general';
    }
  };

  const filteredAnnouncements = announcements.filter(announcement => {
    if (filterType !== 'all' && announcement.type !== filterType.toUpperCase()) return false;
    if (filterPriority !== 'all' && announcement.priority !== filterPriority.toUpperCase()) return false;
    return true;
  });

  if (loading) {
    return (
      <div className="company-announcements loading">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading announcements...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="company-announcements">
      <CorporateNavigation />
      <div className="announcements-header">
        <div className="header-content">
          <h1>Company Announcements</h1>
          <div className="header-stats">
            <span className="stat">Total: {announcements.length}</span>
            <span className="stat published">
              Published: {announcements.filter(a => a.isPublished).length}
            </span>
            <span className="stat pinned">
              Pinned: {announcements.filter(a => a.isPinned).length}
            </span>
          </div>
        </div>
        <button 
          className="create-button"
          onClick={() => setShowCreateModal(true)}
        >
          Create Announcement
        </button>
      </div>

      <div className="filters-section">
        <div className="filter-group">
          <label>Type:</label>
          <select value={filterType} onChange={(e) => setFilterType(e.target.value)}>
            <option value="all">All Types</option>
            <option value="general">General</option>
            <option value="urgent">Urgent</option>
            <option value="event">Event</option>
            <option value="policy">Policy</option>
          </select>
        </div>
        <div className="filter-group">
          <label>Priority:</label>
          <select value={filterPriority} onChange={(e) => setFilterPriority(e.target.value)}>
            <option value="all">All Priorities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
        </div>
      </div>

      {error && (
        <div className="error-banner">
          <p>{error}</p>
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      <div className="announcements-list">
        {filteredAnnouncements.map(announcement => (
          <div key={announcement.id} className="announcement-card">
            <div className="announcement-header">
              <div className="announcement-title">
                {announcement.isPinned && <span className="pin-icon">📌</span>}
                <h3>{announcement.title}</h3>
              </div>
              <div className="announcement-badges">
                <span className={`type-badge ${getTypeColor(announcement.type)}`}>
                  {announcement.type}
                </span>
                <span className={`priority-badge ${getPriorityColor(announcement.priority)}`}>
                  {announcement.priority}
                </span>
                {!announcement.isPublished && (
                  <span className="draft-badge">DRAFT</span>
                )}
              </div>
            </div>
            
            <div className="announcement-content">
              <p>{announcement.content}</p>
            </div>
            
            <div className="announcement-meta">
              <div className="meta-info">
                <span className="author">By: {announcement.authorName || 'Unknown'}</span>
                <span className="created-date">
                  Created: {new Date(announcement.createdAt).toLocaleDateString()}
                </span>
                {announcement.publishedAt && (
                  <span className="published-date">
                    Published: {new Date(announcement.publishedAt).toLocaleDateString()}
                  </span>
                )}
                {announcement.targetDepartmentNames && announcement.targetDepartmentNames.length > 0 && (
                  <span className="target-departments">
                    Departments: {announcement.targetDepartmentNames.join(', ')}
                  </span>
                )}
              </div>
            </div>
            
            <div className="announcement-actions">
              <button 
                className="action-button edit"
                onClick={() => openEditModal(announcement)}
              >
                Edit
              </button>
              <button 
                className={`action-button pin ${announcement.isPinned ? 'pinned' : ''}`}
                onClick={() => handleTogglePin(announcement.id, announcement.isPinned)}
              >
                {announcement.isPinned ? 'Unpin' : 'Pin'}
              </button>
              <button 
                className="action-button delete"
                onClick={() => handleDeleteAnnouncement(announcement.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>

      {filteredAnnouncements.length === 0 && (
        <div className="no-announcements">
          <h3>No Announcements Found</h3>
          <p>
            {filterType === 'all' && filterPriority === 'all'
              ? 'No announcements have been created yet.'
              : 'No announcements match the current filters.'
            }
          </p>
          <button 
            className="create-button"
            onClick={() => setShowCreateModal(true)}
          >
            Create First Announcement
          </button>
        </div>
      )}

      {/* Create/Edit Modal */}
      {(showCreateModal || editingAnnouncement) && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingAnnouncement ? 'Edit Announcement' : 'Create Announcement'}</h2>
              <button className="close-button" onClick={closeModal}>×</button>
            </div>
            
            <form onSubmit={editingAnnouncement ? handleUpdateAnnouncement : handleCreateAnnouncement}>
              <div className="form-group">
                <label htmlFor="title">Title *</label>
                <input
                  type="text"
                  id="title"
                  value={formData.title}
                  onChange={e => setFormData(prev => ({ ...prev, title: e.target.value }))}
                  required
                  placeholder="Enter announcement title"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="content">Content *</label>
                <textarea
                  id="content"
                  value={formData.content}
                  onChange={e => setFormData(prev => ({ ...prev, content: e.target.value }))}
                  required
                  placeholder="Enter announcement content"
                  rows={6}
                />
              </div>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="type">Type</label>
                  <select
                    id="type"
                    value={formData.type}
                    onChange={e => setFormData(prev => ({ ...prev, type: e.target.value as any }))}
                  >
                    <option value="GENERAL">General</option>
                    <option value="URGENT">Urgent</option>
                    <option value="EVENT">Event</option>
                    <option value="POLICY">Policy</option>
                  </select>
                </div>
                
                <div className="form-group">
                  <label htmlFor="priority">Priority</label>
                  <select
                    id="priority"
                    value={formData.priority}
                    onChange={e => setFormData(prev => ({ ...prev, priority: e.target.value as any }))}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </div>
              </div>
              
              <div className="form-group">
                <label>Target Departments (Optional)</label>
                <div className="department-checkboxes">
                  {departments.map(dept => (
                    <label key={dept.id} className="checkbox-item">
                      <input
                        type="checkbox"
                        checked={formData.targetDepartmentIds.includes(dept.id)}
                        onChange={e => {
                          if (e.target.checked) {
                            setFormData(prev => ({
                              ...prev,
                              targetDepartmentIds: [...prev.targetDepartmentIds, dept.id]
                            }));
                          } else {
                            setFormData(prev => ({
                              ...prev,
                              targetDepartmentIds: prev.targetDepartmentIds.filter(id => id !== dept.id)
                            }));
                          }
                        }}
                      />
                      <span>{dept.name}</span>
                    </label>
                  ))}
                </div>
                <small className="help-text">
                  Leave empty to send to all employees
                </small>
              </div>
              
              <div className="form-options">
                <label className="checkbox-item">
                  <input
                    type="checkbox"
                    checked={formData.isPinned}
                    onChange={e => setFormData(prev => ({ ...prev, isPinned: e.target.checked }))}
                  />
                  <span>Pin this announcement</span>
                </label>
                
                <label className="checkbox-item">
                  <input
                    type="checkbox"
                    checked={formData.isPublished}
                    onChange={e => setFormData(prev => ({ ...prev, isPublished: e.target.checked }))}
                  />
                  <span>Publish immediately</span>
                </label>
              </div>
              
              <div className="form-actions">
                <button type="button" className="cancel-button" onClick={closeModal}>
                  Cancel
                </button>
                <button type="submit" className="submit-button">
                  {editingAnnouncement ? 'Update Announcement' : 'Create Announcement'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default CompanyAnnouncements;
