import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './UserInsights.css';

interface UserInsights {
  userId: number;
  userName: string;
  totalMatches: number;
  successfulMatches: number;
  totalMeetings: number;
  completedMeetings: number;
  totalMessages: number;
  averageRating: number;
  profileViews: number;
  profileLikes: number;
  responseRate: number;
  engagementScore: number;
  matchSuccessRate: number;
  meetingAttendanceRate: number;
  averageResponseTime: number;
  streakDays: number;
  topSkills: string[];
  topInterests: string[];
  preferredMeetingTimes: string[];
  feedbackSummary: string;
  recommendations: string[];
  activityTrend: string;
  lastActiveDate: string;
  joinDate: string;
}

interface ActivityData {
  date: string;
  matches: number;
  meetings: number;
  messages: number;
  interactions: number;
}

const UserInsights: React.FC = () => {
  const [insights, setInsights] = useState<UserInsights | null>(null);
  const [activity, setActivity] = useState<ActivityData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedPeriod, setSelectedPeriod] = useState('30');

  useEffect(() => {
    fetchUserInsights();
  }, [selectedPeriod]);

  const fetchUserInsights = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      // Fetch user insights
      const insightsResponse = await axios.get(
        'http://localhost:8080/api/analytics/user/insights',
        { headers }
      );
      setInsights(insightsResponse.data);

      // Fetch user activity data
      const activityResponse = await axios.get(
        `http://localhost:8080/api/analytics/user/activity?days=${selectedPeriod}`,
        { headers }
      );
      setActivity(activityResponse.data);

      setError(null);
    } catch (err) {
      console.error('Error fetching user insights:', err);
      setError('Failed to load user insights');
    } finally {
      setLoading(false);
    }
  };

  const getEngagementLevel = (score: number): { level: string; color: string } => {
    if (score >= 8) return { level: 'Excellent', color: '#38a169' };
    if (score >= 6) return { level: 'Good', color: '#4299e1' };
    if (score >= 4) return { level: 'Average', color: '#ed8936' };
    return { level: 'Low', color: '#e53e3e' };
  };

  const formatPercentage = (num: number): string => {
    return (num * 100).toFixed(1) + '%';
  };

  const formatDuration = (minutes: number): string => {
    if (minutes < 60) return `${minutes}m`;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  };

  if (loading) {
    return (
      <div className="user-insights">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading your insights...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="user-insights">
        <div className="error-container">
          <h2>Error Loading Insights</h2>
          <p>{error}</p>
          <button onClick={fetchUserInsights} className="retry-btn">
            Retry
          </button>
        </div>
      </div>
    );
  }

  const engagementInfo = insights ? getEngagementLevel(insights.engagementScore) : null;

  return (
    <div className="user-insights">
      <div className="insights-header">
        <div className="user-info">
          <h1>Your Insights</h1>
          <p className="welcome-text">
            Welcome back, {insights?.userName}! Here's how you're doing on the platform.
          </p>
        </div>
        <div className="period-selector">
          <label htmlFor="period">Period:</label>
          <select
            id="period"
            value={selectedPeriod}
            onChange={(e) => setSelectedPeriod(e.target.value)}
          >
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 90 days</option>
          </select>
        </div>
      </div>

      {insights && (
        <>
          {/* Engagement Score */}
          <div className="engagement-section">
            <div className="engagement-card">
              <div className="engagement-icon">🎯</div>
              <div className="engagement-content">
                <h3>Engagement Score</h3>
                <div className="engagement-score">
                  <span className="score-value">{insights.engagementScore.toFixed(1)}</span>
                  <span className="score-max">/10</span>
                </div>
                <div 
                  className="engagement-level"
                  style={{ color: engagementInfo?.color }}
                >
                  {engagementInfo?.level}
                </div>
              </div>
              <div className="engagement-visual">
                <div className="score-ring">
                  <svg width="80" height="80" viewBox="0 0 80 80">
                    <circle
                      cx="40"
                      cy="40"
                      r="30"
                      fill="none"
                      stroke="#e2e8f0"
                      strokeWidth="8"
                    />
                    <circle
                      cx="40"
                      cy="40"
                      r="30"
                      fill="none"
                      stroke={engagementInfo?.color}
                      strokeWidth="8"
                      strokeDasharray={`${(insights.engagementScore / 10) * 188.5} 188.5`}
                      strokeLinecap="round"
                      transform="rotate(-90 40 40)"
                    />
                  </svg>
                </div>
              </div>
            </div>
          </div>

          {/* Key Stats */}
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon">💕</div>
              <div className="stat-content">
                <h4>Match Success Rate</h4>
                <div className="stat-value">{formatPercentage(insights.matchSuccessRate)}</div>
                <div className="stat-detail">{insights.successfulMatches} of {insights.totalMatches} matches</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">📅</div>
              <div className="stat-content">
                <h4>Meeting Attendance</h4>
                <div className="stat-value">{formatPercentage(insights.meetingAttendanceRate)}</div>
                <div className="stat-detail">{insights.completedMeetings} of {insights.totalMeetings} meetings</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">⚡</div>
              <div className="stat-content">
                <h4>Response Rate</h4>
                <div className="stat-value">{formatPercentage(insights.responseRate)}</div>
                <div className="stat-detail">Avg: {formatDuration(insights.averageResponseTime)}</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">🔥</div>
              <div className="stat-content">
                <h4>Current Streak</h4>
                <div className="stat-value">{insights.streakDays}</div>
                <div className="stat-detail">days active</div>
              </div>
            </div>
          </div>

          {/* Activity Chart */}
          <div className="activity-section">
            <h2>Your Activity</h2>
            <div className="activity-chart">
              <div className="chart-legend">
                <div className="legend-item">
                  <span className="legend-dot matches"></span>
                  <span>Matches</span>
                </div>
                <div className="legend-item">
                  <span className="legend-dot meetings"></span>
                  <span>Meetings</span>
                </div>
                <div className="legend-item">
                  <span className="legend-dot messages"></span>
                  <span>Messages</span>
                </div>
                <div className="legend-item">
                  <span className="legend-dot interactions"></span>
                  <span>Interactions</span>
                </div>
              </div>
              
              {activity.length > 0 ? (
                <div className="activity-bars">
                  {activity.slice(-14).map((day, index) => (
                    <div key={index} className="activity-day">
                      <div className="day-bars">
                        <div 
                          className="activity-bar matches"
                          style={{ height: `${Math.max((day.matches / Math.max(...activity.map(d => d.matches))) * 100, 5)}%` }}
                          title={`Matches: ${day.matches}`}
                        ></div>
                        <div 
                          className="activity-bar meetings"
                          style={{ height: `${Math.max((day.meetings / Math.max(...activity.map(d => d.meetings))) * 100, 5)}%` }}
                          title={`Meetings: ${day.meetings}`}
                        ></div>
                        <div 
                          className="activity-bar messages"
                          style={{ height: `${Math.max((day.messages / Math.max(...activity.map(d => d.messages))) * 100, 5)}%` }}
                          title={`Messages: ${day.messages}`}
                        ></div>
                        <div 
                          className="activity-bar interactions"
                          style={{ height: `${Math.max((day.interactions / Math.max(...activity.map(d => d.interactions))) * 100, 5)}%` }}
                          title={`Interactions: ${day.interactions}`}
                        ></div>
                      </div>
                      <div className="day-label">
                        {new Date(day.date).toLocaleDateString('en-US', { 
                          weekday: 'short' 
                        })}
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="no-activity">
                  <p>No activity data available for the selected period.</p>
                </div>
              )}
            </div>
          </div>

          {/* Profile Insights */}
          <div className="profile-insights">
            <div className="insights-grid">
              <div className="insight-card">
                <h3>👀 Profile Views</h3>
                <div className="insight-value">{insights.profileViews}</div>
                <div className="insight-trend">
                  {insights.profileLikes} likes received
                </div>
              </div>

              <div className="insight-card">
                <h3>⭐ Average Rating</h3>
                <div className="insight-value">{insights.averageRating.toFixed(1)}</div>
                <div className="insight-trend">
                  Based on feedback received
                </div>
              </div>

              <div className="insight-card">
                <h3>💬 Messages Sent</h3>
                <div className="insight-value">{insights.totalMessages}</div>
                <div className="insight-trend">
                  Keep the conversation going!
                </div>
              </div>
            </div>
          </div>

          {/* Skills and Interests */}
          <div className="preferences-section">
            <div className="preferences-grid">
              <div className="preference-card">
                <h3>🛠️ Top Skills</h3>
                <div className="tags-container">
                  {insights.topSkills.map((skill, index) => (
                    <span key={index} className="tag skill-tag">{skill}</span>
                  ))}
                </div>
              </div>

              <div className="preference-card">
                <h3>❤️ Top Interests</h3>
                <div className="tags-container">
                  {insights.topInterests.map((interest, index) => (
                    <span key={index} className="tag interest-tag">{interest}</span>
                  ))}
                </div>
              </div>

              <div className="preference-card">
                <h3>⏰ Preferred Meeting Times</h3>
                <div className="tags-container">
                  {insights.preferredMeetingTimes.map((time, index) => (
                    <span key={index} className="tag time-tag">{time}</span>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Recommendations */}
          {insights.recommendations.length > 0 && (
            <div className="recommendations-section">
              <h2>💡 Recommendations for You</h2>
              <div className="recommendations-list">
                {insights.recommendations.map((recommendation, index) => (
                  <div key={index} className="recommendation-item">
                    <div className="recommendation-icon">💡</div>
                    <div className="recommendation-text">{recommendation}</div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Feedback Summary */}
          {insights.feedbackSummary && (
            <div className="feedback-section">
              <h2>📝 Feedback Summary</h2>
              <div className="feedback-card">
                <p>{insights.feedbackSummary}</p>
              </div>
            </div>
          )}

          {/* Account Info */}
          <div className="account-info">
            <div className="info-grid">
              <div className="info-item">
                <span className="info-label">Member Since</span>
                <span className="info-value">
                  {new Date(insights.joinDate).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}
                </span>
              </div>
              <div className="info-item">
                <span className="info-label">Last Active</span>
                <span className="info-value">
                  {new Date(insights.lastActiveDate).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric'
                  })}
                </span>
              </div>
              <div className="info-item">
                <span className="info-label">Activity Trend</span>
                <span className={`info-value trend-${insights.activityTrend.toLowerCase()}`}>
                  {insights.activityTrend}
                </span>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default UserInsights;
