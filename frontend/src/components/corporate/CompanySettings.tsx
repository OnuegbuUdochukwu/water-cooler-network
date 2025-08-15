import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './CompanySettings.css';

interface CompanySettingsData {
  id: number;
  companyId: number;
  primaryColor?: string;
  logoUrl?: string;
  allowedEmailDomains?: string;
  domainVerified: boolean;
  enableMatching: boolean;
  enableLounges: boolean;
  enableGamification: boolean;
  enableAnnouncements: boolean;
  enableAnalytics: boolean;
  maxEmployees?: number;
  createdAt: string;
  updatedAt: string;
}

interface Company {
  id: number;
  name: string;
  subscriptionTier: string;
  isActive: boolean;
}

const CompanySettings: React.FC = () => {
  const [settings, setSettings] = useState<CompanySettingsData | null>(null);
  const [company, setCompany] = useState<Company | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    primaryColor: '',
    logoUrl: '',
    allowedEmailDomains: '',
    enableMatching: true,
    enableLounges: true,
    enableGamification: true,
    enableAnnouncements: true,
    enableAnalytics: true,
    maxEmployees: ''
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
      const companyId = userResponse.data.companyId;

      if (!companyId) {
        setError('You are not associated with any company');
        return;
      }

      // Fetch company details
      const companyResponse = await axios.get(`/api/companies/${companyId}`, { headers });
      setCompany(companyResponse.data);

      // Fetch company settings (might not exist yet)
      try {
        const settingsResponse = await axios.get(`/api/companies/${companyId}/settings`, { headers });
        const settingsData = settingsResponse.data;
        setSettings(settingsData);
        
        // Populate form with existing settings
        setFormData({
          primaryColor: settingsData.primaryColor || '',
          logoUrl: settingsData.logoUrl || '',
          allowedEmailDomains: settingsData.allowedEmailDomains || '',
          enableMatching: settingsData.enableMatching,
          enableLounges: settingsData.enableLounges,
          enableGamification: settingsData.enableGamification,
          enableAnnouncements: settingsData.enableAnnouncements,
          enableAnalytics: settingsData.enableAnalytics,
          maxEmployees: settingsData.maxEmployees?.toString() || ''
        });
      } catch (settingsError) {
        // Settings don't exist yet, use defaults
        console.log('No settings found, using defaults');
      }

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load company data');
    } finally {
      setLoading(false);
    }
  };

  const handleSaveSettings = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!company) return;

    try {
      setSaving(true);
      setError(null);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const settingsData = {
        companyId: company.id,
        primaryColor: formData.primaryColor || null,
        logoUrl: formData.logoUrl || null,
        allowedEmailDomains: formData.allowedEmailDomains || null,
        enableMatching: formData.enableMatching,
        enableLounges: formData.enableLounges,
        enableGamification: formData.enableGamification,
        enableAnnouncements: formData.enableAnnouncements,
        enableAnalytics: formData.enableAnalytics,
        maxEmployees: formData.maxEmployees ? parseInt(formData.maxEmployees) : null
      };

      if (settings) {
        // Update existing settings
        await axios.put(`/api/companies/${company.id}/settings`, settingsData, { headers });
      } else {
        // Create new settings
        await axios.post(`/api/companies/${company.id}/settings`, settingsData, { headers });
      }

      setSuccessMessage('Settings saved successfully!');
      setTimeout(() => setSuccessMessage(null), 3000);
      fetchData(); // Refresh data

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save settings');
    } finally {
      setSaving(false);
    }
  };

  const handleInputChange = (field: string, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  if (loading) {
    return (
      <div className="company-settings loading">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading company settings...</p>
        </div>
      </div>
    );
  }

  if (error && !company) {
    return (
      <div className="company-settings error">
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
    <div className="company-settings">
      <CorporateNavigation />
      <div className="settings-header">
        <h1>Company Settings</h1>
        {company && (
          <div className="company-info">
            <span className="company-name">{company.name}</span>
            <span className={`subscription-tier ${company.subscriptionTier.toLowerCase()}`}>
              {company.subscriptionTier}
            </span>
          </div>
        )}
      </div>

      {error && (
        <div className="error-banner">
          <p>{error}</p>
          <button onClick={() => setError(null)}>×</button>
        </div>
      )}

      {successMessage && (
        <div className="success-banner">
          <p>{successMessage}</p>
          <button onClick={() => setSuccessMessage(null)}>×</button>
        </div>
      )}

      <form onSubmit={handleSaveSettings} className="settings-form">
        {/* Branding Section */}
        <div className="settings-section">
          <h2>Branding & Appearance</h2>
          <div className="section-content">
            <div className="form-group">
              <label htmlFor="primaryColor">Primary Color</label>
              <div className="color-input-group">
                <input
                  type="color"
                  id="primaryColor"
                  value={formData.primaryColor || '#007bff'}
                  onChange={e => handleInputChange('primaryColor', e.target.value)}
                />
                <input
                  type="text"
                  value={formData.primaryColor || ''}
                  onChange={e => handleInputChange('primaryColor', e.target.value)}
                  placeholder="#007bff"
                />
              </div>
            </div>
            
            <div className="form-group">
              <label htmlFor="logoUrl">Company Logo URL</label>
              <input
                type="url"
                id="logoUrl"
                value={formData.logoUrl}
                onChange={e => handleInputChange('logoUrl', e.target.value)}
                placeholder="https://example.com/logo.png"
              />
            </div>
          </div>
        </div>

        {/* Security Section */}
        <div className="settings-section">
          <h2>Security & Access</h2>
          <div className="section-content">
            <div className="form-group">
              <label htmlFor="allowedEmailDomains">Allowed Email Domains</label>
              <input
                type="text"
                id="allowedEmailDomains"
                value={formData.allowedEmailDomains}
                onChange={e => handleInputChange('allowedEmailDomains', e.target.value)}
                placeholder="company.com, subsidiary.com"
              />
              <small className="help-text">
                Comma-separated list of email domains allowed to join your company
              </small>
            </div>
            
            <div className="form-group">
              <label htmlFor="maxEmployees">Maximum Employees</label>
              <input
                type="number"
                id="maxEmployees"
                value={formData.maxEmployees}
                onChange={e => handleInputChange('maxEmployees', e.target.value)}
                placeholder="Leave empty for unlimited"
                min="1"
              />
            </div>
          </div>
        </div>

        {/* Features Section */}
        <div className="settings-section">
          <h2>Feature Controls</h2>
          <div className="section-content">
            <div className="feature-toggles">
              <div className="toggle-item">
                <div className="toggle-info">
                  <h4>Coffee Chat Matching</h4>
                  <p>Enable automatic matching of employees for coffee chats</p>
                </div>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={formData.enableMatching}
                    onChange={e => handleInputChange('enableMatching', e.target.checked)}
                  />
                  <span className="slider"></span>
                </label>
              </div>
              
              <div className="toggle-item">
                <div className="toggle-info">
                  <h4>Topic Lounges</h4>
                  <p>Allow employees to create and join topic-based discussion lounges</p>
                </div>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={formData.enableLounges}
                    onChange={e => handleInputChange('enableLounges', e.target.checked)}
                  />
                  <span className="slider"></span>
                </label>
              </div>
              
              <div className="toggle-item">
                <div className="toggle-info">
                  <h4>Gamification</h4>
                  <p>Enable badges, streaks, and achievement tracking</p>
                </div>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={formData.enableGamification}
                    onChange={e => handleInputChange('enableGamification', e.target.checked)}
                  />
                  <span className="slider"></span>
                </label>
              </div>
              
              <div className="toggle-item">
                <div className="toggle-info">
                  <h4>Company Announcements</h4>
                  <p>Allow admins to post company-wide announcements</p>
                </div>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={formData.enableAnnouncements}
                    onChange={e => handleInputChange('enableAnnouncements', e.target.checked)}
                  />
                  <span className="slider"></span>
                </label>
              </div>
              
              <div className="toggle-item">
                <div className="toggle-info">
                  <h4>Analytics</h4>
                  <p>Enable usage analytics and reporting for admins</p>
                </div>
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={formData.enableAnalytics}
                    onChange={e => handleInputChange('enableAnalytics', e.target.checked)}
                  />
                  <span className="slider"></span>
                </label>
              </div>
            </div>
          </div>
        </div>

        {/* Domain Verification Section */}
        {settings && (
          <div className="settings-section">
            <h2>Domain Verification</h2>
            <div className="section-content">
              <div className="verification-status">
                <div className="status-item">
                  <span className="label">Domain Status:</span>
                  <span className={`status ${settings.domainVerified ? 'verified' : 'unverified'}`}>
                    {settings.domainVerified ? 'Verified' : 'Not Verified'}
                  </span>
                </div>
                {!settings.domainVerified && (
                  <div className="verification-help">
                    <p>Domain verification allows enhanced security and branding features.</p>
                    <button type="button" className="verify-button">
                      Start Verification Process
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        <div className="form-actions">
          <button 
            type="submit" 
            className="save-button"
            disabled={saving}
          >
            {saving ? 'Saving...' : 'Save Settings'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CompanySettings;
