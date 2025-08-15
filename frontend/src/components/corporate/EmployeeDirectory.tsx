import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './EmployeeDirectory.css';

interface Employee {
  id: number;
  name: string;
  email: string;
  role: string;
  industry?: string;
  skills?: string[];
  interests?: string[];
  companyId: number;
  departmentName?: string;
  jobTitle?: string;
  isActive: boolean;
  createdAt: string;
}

interface Department {
  id: number;
  name: string;
}

const EmployeeDirectory: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterDepartment, setFilterDepartment] = useState<string>('all');
  const [filterRole, setFilterRole] = useState<string>('all');

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
      const companyId = userResponse.data.companyId;

      if (!companyId) {
        setError('You are not associated with any company');
        return;
      }

      // Fetch company employees
      const employeesResponse = await axios.get(`/api/companies/${companyId}/employees`, { headers });
      setEmployees(employeesResponse.data);

      // Fetch departments
      const departmentsResponse = await axios.get(`/api/departments/company/${companyId}`, { headers });
      setDepartments(departmentsResponse.data);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load employee data');
    } finally {
      setLoading(false);
    }
  };

  const filteredEmployees = employees.filter(employee => {
    const matchesSearch = employee.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         employee.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         employee.jobTitle?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesDepartment = filterDepartment === 'all' || employee.departmentName === filterDepartment;
    const matchesRole = filterRole === 'all' || employee.role === filterRole;
    
    return matchesSearch && matchesDepartment && matchesRole;
  });

  if (loading) {
    return (
      <div className="employee-directory loading">
        <CorporateNavigation />
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading employee directory...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="employee-directory error">
        <CorporateNavigation />
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
    <div className="employee-directory">
      <CorporateNavigation />
      
      <div className="directory-header">
        <h1>Employee Directory</h1>
        <div className="employee-count">
          {filteredEmployees.length} of {employees.length} employees
        </div>
      </div>

      <div className="directory-filters">
        <div className="search-group">
          <input
            type="text"
            placeholder="Search employees..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
        
        <div className="filter-group">
          <label>Department:</label>
          <select 
            value={filterDepartment} 
            onChange={(e) => setFilterDepartment(e.target.value)}
          >
            <option value="all">All Departments</option>
            {departments.map(dept => (
              <option key={dept.id} value={dept.name}>{dept.name}</option>
            ))}
          </select>
        </div>
        
        <div className="filter-group">
          <label>Role:</label>
          <select 
            value={filterRole} 
            onChange={(e) => setFilterRole(e.target.value)}
          >
            <option value="all">All Roles</option>
            <option value="USER">User</option>
            <option value="CORPORATE_ADMIN">Corporate Admin</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>
      </div>

      <div className="employees-grid">
        {filteredEmployees.map(employee => (
          <div key={employee.id} className="employee-card">
            <div className="employee-header">
              <div className="employee-avatar">
                {employee.name.charAt(0).toUpperCase()}
              </div>
              <div className="employee-info">
                <h3>{employee.name}</h3>
                <p className="employee-email">{employee.email}</p>
                {employee.jobTitle && (
                  <p className="job-title">{employee.jobTitle}</p>
                )}
              </div>
              <span className={`role-badge ${employee.role.toLowerCase()}`}>
                {employee.role}
              </span>
            </div>
            
            <div className="employee-details">
              {employee.departmentName && (
                <div className="detail-item">
                  <span className="label">Department:</span>
                  <span className="value">{employee.departmentName}</span>
                </div>
              )}
              {employee.industry && (
                <div className="detail-item">
                  <span className="label">Industry:</span>
                  <span className="value">{employee.industry}</span>
                </div>
              )}
              <div className="detail-item">
                <span className="label">Joined:</span>
                <span className="value">{new Date(employee.createdAt).toLocaleDateString()}</span>
              </div>
            </div>
            
            {(employee.skills || employee.interests) && (
              <div className="employee-tags">
                {employee.skills && employee.skills.length > 0 && (
                  <div className="tag-group">
                    <span className="tag-label">Skills:</span>
                    <div className="tags">
                      {employee.skills.slice(0, 3).map((skill, index) => (
                        <span key={index} className="tag skill">{skill}</span>
                      ))}
                      {employee.skills.length > 3 && (
                        <span className="tag more">+{employee.skills.length - 3}</span>
                      )}
                    </div>
                  </div>
                )}
                {employee.interests && employee.interests.length > 0 && (
                  <div className="tag-group">
                    <span className="tag-label">Interests:</span>
                    <div className="tags">
                      {employee.interests.slice(0, 3).map((interest, index) => (
                        <span key={index} className="tag interest">{interest}</span>
                      ))}
                      {employee.interests.length > 3 && (
                        <span className="tag more">+{employee.interests.length - 3}</span>
                      )}
                    </div>
                  </div>
                )}
              </div>
            )}
            
            <div className="employee-actions">
              <button className="action-button message">
                Message
              </button>
              <button className="action-button profile">
                View Profile
              </button>
            </div>
          </div>
        ))}
      </div>

      {filteredEmployees.length === 0 && employees.length > 0 && (
        <div className="no-results">
          <h3>No employees found</h3>
          <p>Try adjusting your search or filter criteria.</p>
        </div>
      )}

      {employees.length === 0 && (
        <div className="no-employees">
          <h3>No Employees Found</h3>
          <p>Your company doesn't have any employees yet.</p>
        </div>
      )}
    </div>
  );
};

export default EmployeeDirectory;
