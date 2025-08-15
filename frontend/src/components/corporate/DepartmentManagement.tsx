import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './DepartmentManagement.css';

interface Department {
  id: number;
  name: string;
  description: string;
  companyId: number;
  headUserId?: number;
  headUserName?: string;
  parentDepartmentId?: number;
  parentDepartmentName?: string;
  memberCount?: number;
  isActive: boolean;
  createdAt: string;
}

interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

interface CreateDepartmentData {
  name: string;
  description: string;
  companyId: number;
  headUserId?: number;
  parentDepartmentId?: number;
}

const DepartmentManagement: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingDepartment, setEditingDepartment] = useState<Department | null>(null);
  const [companyId, setCompanyId] = useState<number | null>(null);

  const [formData, setFormData] = useState<CreateDepartmentData>({
    name: '',
    description: '',
    companyId: 0,
    headUserId: undefined,
    parentDepartmentId: undefined
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
      setFormData(prev => ({ ...prev, companyId: userCompanyId }));

      // Fetch departments
      const departmentsResponse = await axios.get(`/api/departments/company/${userCompanyId}`, { headers });
      setDepartments(departmentsResponse.data);

      // Fetch company users for head selection
      const usersResponse = await axios.get(`/api/companies/${userCompanyId}/employees`, { headers });
      setUsers(usersResponse.data);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateDepartment = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.post('/api/departments', formData, { headers });
      
      setShowCreateModal(false);
      setFormData({
        name: '',
        description: '',
        companyId: companyId || 0,
        headUserId: undefined,
        parentDepartmentId: undefined
      });
      
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create department');
    }
  };

  const handleUpdateDepartment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingDepartment) return;

    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.put(`/api/departments/${editingDepartment.id}`, {
        name: formData.name,
        description: formData.description,
        headUserId: formData.headUserId
      }, { headers });
      
      setEditingDepartment(null);
      setFormData({
        name: '',
        description: '',
        companyId: companyId || 0,
        headUserId: undefined,
        parentDepartmentId: undefined
      });
      
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update department');
    }
  };

  const handleDeleteDepartment = async (departmentId: number) => {
    if (!window.confirm('Are you sure you want to delete this department?')) return;

    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      await axios.delete(`/api/departments/${departmentId}`, { headers });
      fetchData(); // Refresh data
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete department');
    }
  };

  const openEditModal = (department: Department) => {
    setEditingDepartment(department);
    setFormData({
      name: department.name,
      description: department.description,
      companyId: department.companyId,
      headUserId: department.headUserId,
      parentDepartmentId: department.parentDepartmentId
    });
  };

  const closeModal = () => {
    setShowCreateModal(false);
    setEditingDepartment(null);
    setFormData({
      name: '',
      description: '',
      companyId: companyId || 0,
      headUserId: undefined,
      parentDepartmentId: undefined
    });
  };

  if (loading) {
    return (
      <div className="department-management loading">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading departments...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="department-management error">
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
    <div className="department-management">
      <CorporateNavigation />
      <div className="management-header">
        <h1>Department Management</h1>
        <button 
          className="create-button"
          onClick={() => setShowCreateModal(true)}
        >
          Create Department
        </button>
      </div>

      <div className="departments-grid">
        {departments.map(department => (
          <div key={department.id} className="department-card">
            <div className="department-header">
              <h3>{department.name}</h3>
              <span className={`status ${department.isActive ? 'active' : 'inactive'}`}>
                {department.isActive ? 'Active' : 'Inactive'}
              </span>
            </div>
            
            <p className="department-description">{department.description}</p>
            
            <div className="department-info">
              <div className="info-item">
                <span className="label">Members:</span>
                <span className="value">{department.memberCount || 0}</span>
              </div>
              {department.headUserName && (
                <div className="info-item">
                  <span className="label">Head:</span>
                  <span className="value">{department.headUserName}</span>
                </div>
              )}
              {department.parentDepartmentName && (
                <div className="info-item">
                  <span className="label">Parent:</span>
                  <span className="value">{department.parentDepartmentName}</span>
                </div>
              )}
              <div className="info-item">
                <span className="label">Created:</span>
                <span className="value">{new Date(department.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
            
            <div className="department-actions">
              <button 
                className="action-button edit"
                onClick={() => openEditModal(department)}
              >
                Edit
              </button>
              <button 
                className="action-button view"
                onClick={() => {/* Navigate to department details */}}
              >
                View Members
              </button>
              <button 
                className="action-button delete"
                onClick={() => handleDeleteDepartment(department.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>

      {departments.length === 0 && (
        <div className="no-departments">
          <h3>No Departments Found</h3>
          <p>Create your first department to get started with organizing your company.</p>
          <button 
            className="create-button"
            onClick={() => setShowCreateModal(true)}
          >
            Create First Department
          </button>
        </div>
      )}

      {/* Create/Edit Modal */}
      {(showCreateModal || editingDepartment) && (
        <div className="modal-overlay" onClick={closeModal}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingDepartment ? 'Edit Department' : 'Create Department'}</h2>
              <button className="close-button" onClick={closeModal}>×</button>
            </div>
            
            <form onSubmit={editingDepartment ? handleUpdateDepartment : handleCreateDepartment}>
              <div className="form-group">
                <label htmlFor="name">Department Name *</label>
                <input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={e => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  required
                  placeholder="Enter department name"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  value={formData.description}
                  onChange={e => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  placeholder="Enter department description"
                  rows={3}
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="headUserId">Department Head</label>
                <select
                  id="headUserId"
                  value={formData.headUserId || ''}
                  onChange={e => setFormData(prev => ({ 
                    ...prev, 
                    headUserId: e.target.value ? parseInt(e.target.value) : undefined 
                  }))}
                >
                  <option value="">Select department head (optional)</option>
                  {users.map(user => (
                    <option key={user.id} value={user.id}>
                      {user.name} ({user.email})
                    </option>
                  ))}
                </select>
              </div>
              
              {!editingDepartment && (
                <div className="form-group">
                  <label htmlFor="parentDepartmentId">Parent Department</label>
                  <select
                    id="parentDepartmentId"
                    value={formData.parentDepartmentId || ''}
                    onChange={e => setFormData(prev => ({ 
                      ...prev, 
                      parentDepartmentId: e.target.value ? parseInt(e.target.value) : undefined 
                    }))}
                  >
                    <option value="">No parent department</option>
                    {departments.map(dept => (
                      <option key={dept.id} value={dept.id}>
                        {dept.name}
                      </option>
                    ))}
                  </select>
                </div>
              )}
              
              <div className="form-actions">
                <button type="button" className="cancel-button" onClick={closeModal}>
                  Cancel
                </button>
                <button type="submit" className="submit-button">
                  {editingDepartment ? 'Update Department' : 'Create Department'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DepartmentManagement;
