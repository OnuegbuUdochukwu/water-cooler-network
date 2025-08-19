import React, { useState, useEffect } from 'react';
import './NotificationPreferences.css';

interface NotificationPreferences {
  id?: number;
  userId: number;
  emailEnabled: boolean;
  emailMatchNotifications: boolean;
  emailMeetingReminders: boolean;
  emailBadgeNotifications: boolean;
  emailLoungeInvitations: boolean;
  emailSystemAnnouncements: boolean;
  emailCorporateAnnouncements: boolean;
  pushEnabled: boolean;
  pushMatchNotifications: boolean;
  pushMeetingReminders: boolean;
  pushBadgeNotifications: boolean;
  pushLoungeInvitations: boolean;
  pushSystemAnnouncements: boolean;
  pushCorporateAnnouncements: boolean;
  inAppEnabled: boolean;
  inAppMatchNotifications: boolean;
  inAppMeetingReminders: boolean;
  inAppBadgeNotifications: boolean;
  inAppLoungeInvitations: boolean;
  inAppSystemAnnouncements: boolean;
  inAppCorporateAnnouncements: boolean;
  quietHoursEnabled: boolean;
  quietHoursStart: string;
  quietHoursEnd: string;
  timezone: string;
  language: string;
}

const NotificationPreferences: React.FC = () => {
  const [preferences, setPreferences] = useState<NotificationPreferences>({
    userId: 1,
    emailEnabled: true,
    emailMatchNotifications: true,
    emailMeetingReminders: true,
    emailBadgeNotifications: true,
    emailLoungeInvitations: true,
    emailSystemAnnouncements: true,
    emailCorporateAnnouncements: true,
    pushEnabled: true,
    pushMatchNotifications: true,
    pushMeetingReminders: true,
    pushBadgeNotifications: true,
    pushLoungeInvitations: true,
    pushSystemAnnouncements: true,
    pushCorporateAnnouncements: true,
    inAppEnabled: true,
    inAppMatchNotifications: true,
    inAppMeetingReminders: true,
    inAppBadgeNotifications: true,
    inAppLoungeInvitations: true,
    inAppSystemAnnouncements: true,
    inAppCorporateAnnouncements: true,
    quietHoursEnabled: false,
    quietHoursStart: '22:00',
    quietHoursEnd: '08:00',
    timezone: 'UTC',
    language: 'en'
  });
  
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchPreferences();
  }, []);

  const fetchPreferences = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/notification-preferences', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setPreferences(data);
      } else {
        // Use default preferences if fetch fails
        console.log('Using default notification preferences');
      }
    } catch (error) {
      console.error('Error fetching preferences:', error);
      // Use default preferences on error
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setMessage('');
    
    try {
      const response = await fetch('/api/notification-preferences', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(preferences),
      });

      if (response.ok) {
        setMessage('Preferences saved successfully!');
        setTimeout(() => setMessage(''), 3000);
      } else {
        setMessage('Failed to save preferences. Please try again.');
      }
    } catch (error) {
      console.error('Error saving preferences:', error);
      setMessage('Error saving preferences. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleReset = async () => {
    if (window.confirm('Are you sure you want to reset all notification preferences to defaults?')) {
      setLoading(true);
      try {
        const response = await fetch('/api/notification-preferences/reset', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
          },
        });

        if (response.ok) {
          const data = await response.json();
          setPreferences(data);
          setMessage('Preferences reset to defaults!');
          setTimeout(() => setMessage(''), 3000);
        }
      } catch (error) {
        console.error('Error resetting preferences:', error);
        setMessage('Error resetting preferences. Please try again.');
      } finally {
        setLoading(false);
      }
    }
  };

  const handleToggle = (key: keyof NotificationPreferences) => {
    setPreferences(prev => ({
      ...prev,
      [key]: !prev[key]
    }));
  };

  const handleInputChange = (key: keyof NotificationPreferences, value: string | boolean) => {
    setPreferences(prev => ({
      ...prev,
      [key]: value
    }));
  };

  if (loading) {
    return <div className="notification-preferences-loading">Loading preferences...</div>;
  }

  return (
    <div className="notification-preferences">
      <div className="preferences-header">
        <h2>Notification Preferences</h2>
        <p>Customize how and when you receive notifications</p>
      </div>

      {message && (
        <div className={`message ${message.includes('successfully') ? 'success' : 'error'}`}>
          {message}
        </div>
      )}

      <div className="preferences-sections">
        {/* Email Notifications */}
        <div className="preference-section">
          <h3>Email Notifications</h3>
          <div className="preference-item">
            <label>
              <input
                type="checkbox"
                checked={preferences.emailEnabled}
                onChange={() => handleToggle('emailEnabled')}
              />
              Enable email notifications
            </label>
          </div>
          
          {preferences.emailEnabled && (
            <div className="sub-preferences">
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.emailMatchNotifications}
                    onChange={() => handleToggle('emailMatchNotifications')}
                  />
                  New matches
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.emailMeetingReminders}
                    onChange={() => handleToggle('emailMeetingReminders')}
                  />
                  Meeting reminders
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.emailBadgeNotifications}
                    onChange={() => handleToggle('emailBadgeNotifications')}
                  />
                  Badge achievements
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.emailLoungeInvitations}
                    onChange={() => handleToggle('emailLoungeInvitations')}
                  />
                  Lounge invitations
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.emailSystemAnnouncements}
                    onChange={() => handleToggle('emailSystemAnnouncements')}
                  />
                  System announcements
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.emailCorporateAnnouncements}
                    onChange={() => handleToggle('emailCorporateAnnouncements')}
                  />
                  Company announcements
                </label>
              </div>
            </div>
          )}
        </div>

        {/* Push Notifications */}
        <div className="preference-section">
          <h3>Push Notifications</h3>
          <div className="preference-item">
            <label>
              <input
                type="checkbox"
                checked={preferences.pushEnabled}
                onChange={() => handleToggle('pushEnabled')}
              />
              Enable push notifications
            </label>
          </div>
          
          {preferences.pushEnabled && (
            <div className="sub-preferences">
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.pushMatchNotifications}
                    onChange={() => handleToggle('pushMatchNotifications')}
                  />
                  New matches
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.pushMeetingReminders}
                    onChange={() => handleToggle('pushMeetingReminders')}
                  />
                  Meeting reminders
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.pushBadgeNotifications}
                    onChange={() => handleToggle('pushBadgeNotifications')}
                  />
                  Badge achievements
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.pushLoungeInvitations}
                    onChange={() => handleToggle('pushLoungeInvitations')}
                  />
                  Lounge invitations
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.pushSystemAnnouncements}
                    onChange={() => handleToggle('pushSystemAnnouncements')}
                  />
                  System announcements
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.pushCorporateAnnouncements}
                    onChange={() => handleToggle('pushCorporateAnnouncements')}
                  />
                  Company announcements
                </label>
              </div>
            </div>
          )}
        </div>

        {/* In-App Notifications */}
        <div className="preference-section">
          <h3>In-App Notifications</h3>
          <div className="preference-item">
            <label>
              <input
                type="checkbox"
                checked={preferences.inAppEnabled}
                onChange={() => handleToggle('inAppEnabled')}
              />
              Enable in-app notifications
            </label>
          </div>
          
          {preferences.inAppEnabled && (
            <div className="sub-preferences">
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.inAppMatchNotifications}
                    onChange={() => handleToggle('inAppMatchNotifications')}
                  />
                  New matches
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.inAppMeetingReminders}
                    onChange={() => handleToggle('inAppMeetingReminders')}
                  />
                  Meeting reminders
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.inAppBadgeNotifications}
                    onChange={() => handleToggle('inAppBadgeNotifications')}
                  />
                  Badge achievements
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.inAppLoungeInvitations}
                    onChange={() => handleToggle('inAppLoungeInvitations')}
                  />
                  Lounge invitations
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.inAppSystemAnnouncements}
                    onChange={() => handleToggle('inAppSystemAnnouncements')}
                  />
                  System announcements
                </label>
              </div>
              <div className="preference-item">
                <label>
                  <input
                    type="checkbox"
                    checked={preferences.inAppCorporateAnnouncements}
                    onChange={() => handleToggle('inAppCorporateAnnouncements')}
                  />
                  Company announcements
                </label>
              </div>
            </div>
          )}
        </div>

        {/* General Preferences */}
        <div className="preference-section">
          <h3>General Preferences</h3>
          
          <div className="preference-item">
            <label>
              <input
                type="checkbox"
                checked={preferences.quietHoursEnabled}
                onChange={() => handleToggle('quietHoursEnabled')}
              />
              Enable quiet hours
            </label>
          </div>
          
          {preferences.quietHoursEnabled && (
            <div className="sub-preferences">
              <div className="time-inputs">
                <div className="time-input">
                  <label>Start Time:</label>
                  <input
                    type="time"
                    value={preferences.quietHoursStart}
                    onChange={(e) => handleInputChange('quietHoursStart', e.target.value)}
                  />
                </div>
                <div className="time-input">
                  <label>End Time:</label>
                  <input
                    type="time"
                    value={preferences.quietHoursEnd}
                    onChange={(e) => handleInputChange('quietHoursEnd', e.target.value)}
                  />
                </div>
              </div>
            </div>
          )}
          
          <div className="preference-item">
            <label>Timezone:</label>
            <select
              value={preferences.timezone}
              onChange={(e) => handleInputChange('timezone', e.target.value)}
            >
              <option value="UTC">UTC</option>
              <option value="America/New_York">Eastern Time</option>
              <option value="America/Chicago">Central Time</option>
              <option value="America/Denver">Mountain Time</option>
              <option value="America/Los_Angeles">Pacific Time</option>
              <option value="Europe/London">London</option>
              <option value="Europe/Paris">Paris</option>
              <option value="Asia/Tokyo">Tokyo</option>
            </select>
          </div>
          
          <div className="preference-item">
            <label>Language:</label>
            <select
              value={preferences.language}
              onChange={(e) => handleInputChange('language', e.target.value)}
            >
              <option value="en">English</option>
              <option value="es">Spanish</option>
              <option value="fr">French</option>
              <option value="de">German</option>
              <option value="ja">Japanese</option>
            </select>
          </div>
        </div>
      </div>

      <div className="preferences-actions">
        <button 
          className="save-btn" 
          onClick={handleSave} 
          disabled={saving}
        >
          {saving ? 'Saving...' : 'Save Preferences'}
        </button>
        <button 
          className="reset-btn" 
          onClick={handleReset}
          disabled={loading}
        >
          Reset to Defaults
        </button>
      </div>
    </div>
  );
};

export default NotificationPreferences;
