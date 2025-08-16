import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './MeetingScheduler.css';

interface ScheduledMeeting {
  id: number;
  matchId: number;
  participantName: string;
  scheduledTime: string;
  duration: number;
  location: string;
  meetingType: string;
  status: string;
  notes: string;
}

interface TimeSlot {
  startTime: string;
  endTime: string;
  available: boolean;
}

interface MeetingRequest {
  matchId: number;
  scheduledTime: string;
  duration: number;
  location: string;
  meetingType: string;
  notes: string;
}

const MeetingScheduler: React.FC = () => {
  const [meetings, setMeetings] = useState<ScheduledMeeting[]>([]);
  const [availableSlots, setAvailableSlots] = useState<TimeSlot[]>([]);
  const [loading, setLoading] = useState(true);
  const [showScheduleForm, setShowScheduleForm] = useState(false);
  const [selectedMatch, setSelectedMatch] = useState<number | null>(null);
  const [meetingForm, setMeetingForm] = useState<MeetingRequest>({
    matchId: 0,
    scheduledTime: '',
    duration: 30,
    location: '',
    meetingType: 'COFFEE_CHAT',
    notes: ''
  });

  useEffect(() => {
    fetchMeetings();
    fetchAvailableSlots();
  }, []);

  const fetchMeetings = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('/api/meetings/my-meetings', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMeetings(response.data);
    } catch (error) {
      console.error('Error fetching meetings:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableSlots = async () => {
    try {
      const token = localStorage.getItem('token');
      const today = new Date().toISOString().split('T')[0];
      const response = await axios.get(`/api/meetings/available-slots?date=${today}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setAvailableSlots(response.data);
    } catch (error) {
      console.error('Error fetching available slots:', error);
    }
  };

  const handleScheduleMeeting = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      await axios.post('/api/meetings/schedule', meetingForm, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      setShowScheduleForm(false);
      setMeetingForm({
        matchId: 0,
        scheduledTime: '',
        duration: 30,
        location: '',
        meetingType: 'COFFEE_CHAT',
        notes: ''
      });
      fetchMeetings();
    } catch (error) {
      console.error('Error scheduling meeting:', error);
    }
  };

  const handleCancelMeeting = async (meetingId: number) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(`/api/meetings/${meetingId}/cancel`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchMeetings();
    } catch (error) {
      console.error('Error cancelling meeting:', error);
    }
  };

  const handleStartMeeting = async (meetingId: number) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(`/api/meetings/${meetingId}/start`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchMeetings();
    } catch (error) {
      console.error('Error starting meeting:', error);
    }
  };

  const handleCompleteMeeting = async (meetingId: number) => {
    try {
      const token = localStorage.getItem('token');
      await axios.put(`/api/meetings/${meetingId}/complete`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      fetchMeetings();
    } catch (error) {
      console.error('Error completing meeting:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return '#3498db';
      case 'IN_PROGRESS': return '#f39c12';
      case 'COMPLETED': return '#27ae60';
      case 'CANCELLED': return '#e74c3c';
      default: return '#95a5a6';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'SCHEDULED': return '📅';
      case 'IN_PROGRESS': return '🔄';
      case 'COMPLETED': return '✅';
      case 'CANCELLED': return '❌';
      default: return '❓';
    }
  };

  const formatDateTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleString();
  };

  const isUpcoming = (dateTime: string) => {
    return new Date(dateTime) > new Date();
  };

  if (loading) {
    return (
      <div className="meeting-scheduler-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading your meetings...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="meeting-scheduler-container">
      <div className="scheduler-header">
        <h1>📅 Meeting Scheduler</h1>
        <p>Manage your coffee chats and networking meetings</p>
        <button 
          className="btn btn-primary"
          onClick={() => setShowScheduleForm(true)}
        >
          Schedule New Meeting
        </button>
      </div>

      <div className="meetings-section">
        <h2>Your Meetings</h2>
        {meetings.length === 0 ? (
          <div className="no-meetings">
            <p>No meetings scheduled yet. Start by scheduling your first coffee chat!</p>
          </div>
        ) : (
          <div className="meetings-list">
            {meetings.map((meeting) => (
              <div key={meeting.id} className="meeting-card">
                <div className="meeting-header">
                  <div className="meeting-info">
                    <h3>{meeting.participantName}</h3>
                    <p className="meeting-time">{formatDateTime(meeting.scheduledTime)}</p>
                  </div>
                  <div 
                    className="meeting-status"
                    style={{ backgroundColor: getStatusColor(meeting.status) }}
                  >
                    <span className="status-icon">{getStatusIcon(meeting.status)}</span>
                    {meeting.status.replace('_', ' ')}
                  </div>
                </div>

                <div className="meeting-details">
                  <div className="detail-item">
                    <strong>Type:</strong> {meeting.meetingType.replace('_', ' ')}
                  </div>
                  <div className="detail-item">
                    <strong>Duration:</strong> {meeting.duration} minutes
                  </div>
                  <div className="detail-item">
                    <strong>Location:</strong> {meeting.location || 'Virtual'}
                  </div>
                  {meeting.notes && (
                    <div className="detail-item">
                      <strong>Notes:</strong> {meeting.notes}
                    </div>
                  )}
                </div>

                <div className="meeting-actions">
                  {meeting.status === 'SCHEDULED' && isUpcoming(meeting.scheduledTime) && (
                    <>
                      <button 
                        className="btn btn-success"
                        onClick={() => handleStartMeeting(meeting.id)}
                      >
                        Start Meeting
                      </button>
                      <button 
                        className="btn btn-danger"
                        onClick={() => handleCancelMeeting(meeting.id)}
                      >
                        Cancel
                      </button>
                    </>
                  )}
                  {meeting.status === 'IN_PROGRESS' && (
                    <button 
                      className="btn btn-primary"
                      onClick={() => handleCompleteMeeting(meeting.id)}
                    >
                      Mark Complete
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Schedule Meeting Modal */}
      {showScheduleForm && (
        <div className="modal-overlay" onClick={() => setShowScheduleForm(false)}>
          <div className="schedule-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Schedule New Meeting</h3>
              <button 
                className="close-btn"
                onClick={() => setShowScheduleForm(false)}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleScheduleMeeting} className="schedule-form">
              <div className="form-group">
                <label>Match ID</label>
                <input
                  type="number"
                  value={meetingForm.matchId}
                  onChange={(e) => setMeetingForm({
                    ...meetingForm,
                    matchId: parseInt(e.target.value)
                  })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Date & Time</label>
                <input
                  type="datetime-local"
                  value={meetingForm.scheduledTime}
                  onChange={(e) => setMeetingForm({
                    ...meetingForm,
                    scheduledTime: e.target.value
                  })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Duration (minutes)</label>
                <select
                  value={meetingForm.duration}
                  onChange={(e) => setMeetingForm({
                    ...meetingForm,
                    duration: parseInt(e.target.value)
                  })}
                >
                  <option value={15}>15 minutes</option>
                  <option value={30}>30 minutes</option>
                  <option value={45}>45 minutes</option>
                  <option value={60}>1 hour</option>
                </select>
              </div>

              <div className="form-group">
                <label>Meeting Type</label>
                <select
                  value={meetingForm.meetingType}
                  onChange={(e) => setMeetingForm({
                    ...meetingForm,
                    meetingType: e.target.value
                  })}
                >
                  <option value="COFFEE_CHAT">Coffee Chat</option>
                  <option value="VIRTUAL_MEETING">Virtual Meeting</option>
                  <option value="LUNCH_MEETING">Lunch Meeting</option>
                  <option value="NETWORKING">Networking</option>
                </select>
              </div>

              <div className="form-group">
                <label>Location</label>
                <input
                  type="text"
                  value={meetingForm.location}
                  onChange={(e) => setMeetingForm({
                    ...meetingForm,
                    location: e.target.value
                  })}
                  placeholder="e.g., Starbucks Downtown, Zoom, etc."
                />
              </div>

              <div className="form-group">
                <label>Notes</label>
                <textarea
                  value={meetingForm.notes}
                  onChange={(e) => setMeetingForm({
                    ...meetingForm,
                    notes: e.target.value
                  })}
                  placeholder="Any additional notes or agenda items..."
                  rows={3}
                />
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowScheduleForm(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Schedule Meeting
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Available Time Slots */}
      <div className="time-slots-section">
        <h2>Available Time Slots Today</h2>
        <div className="time-slots-grid">
          {availableSlots.map((slot, index) => (
            <div 
              key={index} 
              className={`time-slot ${slot.available ? 'available' : 'unavailable'}`}
            >
              <div className="slot-time">
                {new Date(slot.startTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})} - 
                {new Date(slot.endTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
              </div>
              <div className="slot-status">
                {slot.available ? '✅ Available' : '❌ Busy'}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default MeetingScheduler;
