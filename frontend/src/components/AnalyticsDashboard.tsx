import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './AnalyticsDashboard.css';

interface AnalyticsOverview {
  totalUsers: number;
  activeUsers: number;
  totalMatches: number;
  successfulMatches: number;
  totalMeetings: number;
  completedMeetings: number;
  totalMessages: number;
  averageRating: number;
  newUsersToday: number;
  newMatchesToday: number;
  newMeetingsToday: number;
  newMessagesToday: number;
  userGrowthRate: number;
  matchSuccessRate: number;
  meetingCompletionRate: number;
  userEngagementScore: number;
}

interface TrendData {
  date: string;
  users: number;
  matches: number;
  meetings: number;
  messages: number;
}

const AnalyticsDashboard: React.FC = () => {
  const [overview, setOverview] = useState<AnalyticsOverview | null>(null);
  const [trends, setTrends] = useState<TrendData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dateRange, setDateRange] = useState('7'); // Default to 7 days

  useEffect(() => {
    fetchAnalyticsData();
  }, [dateRange]);

  const fetchAnalyticsData = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      // Fetch overview data
      const overviewResponse = await axios.get(
        'http://localhost:8080/api/analytics/overview',
        { headers }
      );
      setOverview(overviewResponse.data);

      // Fetch trend data
      const trendsResponse = await axios.get(
        `http://localhost:8080/api/analytics/trends?days=${dateRange}`,
        { headers }
      );
      setTrends(trendsResponse.data);

      setError(null);
    } catch (err) {
      console.error('Error fetching analytics data:', err);
      setError('Failed to load analytics data');
    } finally {
      setLoading(false);
    }
  };

  const formatNumber = (num: number): string => {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
  };

  const formatPercentage = (num: number): string => {
    return (num * 100).toFixed(1) + '%';
  };

  if (loading) {
    return (
      <div className="analytics-dashboard">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading analytics data...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="analytics-dashboard">
        <div className="error-container">
          <h2>Error Loading Analytics</h2>
          <p>{error}</p>
          <button onClick={fetchAnalyticsData} className="retry-btn">
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="analytics-dashboard">
      <div className="dashboard-header">
        <h1>Analytics Dashboard</h1>
        <div className="date-range-selector">
          <label htmlFor="dateRange">Time Range:</label>
          <select
            id="dateRange"
            value={dateRange}
            onChange={(e) => setDateRange(e.target.value)}
          >
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 90 days</option>
            <option value="365">Last year</option>
          </select>
        </div>
      </div>

      {overview && (
        <>
          {/* Key Metrics Cards */}
          <div className="metrics-grid">
            <div className="metric-card">
              <div className="metric-icon users-icon">👥</div>
              <div className="metric-content">
                <h3>Total Users</h3>
                <div className="metric-value">{formatNumber(overview.totalUsers)}</div>
                <div className="metric-change positive">
                  +{overview.newUsersToday} today
                </div>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon matches-icon">💕</div>
              <div className="metric-content">
                <h3>Total Matches</h3>
                <div className="metric-value">{formatNumber(overview.totalMatches)}</div>
                <div className="metric-change positive">
                  +{overview.newMatchesToday} today
                </div>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon meetings-icon">📅</div>
              <div className="metric-content">
                <h3>Total Meetings</h3>
                <div className="metric-value">{formatNumber(overview.totalMeetings)}</div>
                <div className="metric-change positive">
                  +{overview.newMeetingsToday} today
                </div>
              </div>
            </div>

            <div className="metric-card">
              <div className="metric-icon messages-icon">💬</div>
              <div className="metric-content">
                <h3>Total Messages</h3>
                <div className="metric-value">{formatNumber(overview.totalMessages)}</div>
                <div className="metric-change positive">
                  +{overview.newMessagesToday} today
                </div>
              </div>
            </div>
          </div>

          {/* Performance Metrics */}
          <div className="performance-grid">
            <div className="performance-card">
              <h3>User Growth Rate</h3>
              <div className="performance-value">
                {formatPercentage(overview.userGrowthRate)}
              </div>
              <div className="performance-bar">
                <div 
                  className="performance-fill growth"
                  style={{ width: `${Math.min(overview.userGrowthRate * 100, 100)}%` }}
                ></div>
              </div>
            </div>

            <div className="performance-card">
              <h3>Match Success Rate</h3>
              <div className="performance-value">
                {formatPercentage(overview.matchSuccessRate)}
              </div>
              <div className="performance-bar">
                <div 
                  className="performance-fill success"
                  style={{ width: `${overview.matchSuccessRate * 100}%` }}
                ></div>
              </div>
            </div>

            <div className="performance-card">
              <h3>Meeting Completion Rate</h3>
              <div className="performance-value">
                {formatPercentage(overview.meetingCompletionRate)}
              </div>
              <div className="performance-bar">
                <div 
                  className="performance-fill completion"
                  style={{ width: `${overview.meetingCompletionRate * 100}%` }}
                ></div>
              </div>
            </div>

            <div className="performance-card">
              <h3>User Engagement Score</h3>
              <div className="performance-value">
                {overview.userEngagementScore.toFixed(1)}/10
              </div>
              <div className="performance-bar">
                <div 
                  className="performance-fill engagement"
                  style={{ width: `${(overview.userEngagementScore / 10) * 100}%` }}
                ></div>
              </div>
            </div>
          </div>

          {/* Trends Chart */}
          <div className="trends-section">
            <h2>Activity Trends</h2>
            <div className="trends-chart">
              <div className="chart-legend">
                <div className="legend-item">
                  <span className="legend-color users"></span>
                  <span>Users</span>
                </div>
                <div className="legend-item">
                  <span className="legend-color matches"></span>
                  <span>Matches</span>
                </div>
                <div className="legend-item">
                  <span className="legend-color meetings"></span>
                  <span>Meetings</span>
                </div>
                <div className="legend-item">
                  <span className="legend-color messages"></span>
                  <span>Messages</span>
                </div>
              </div>
              
              <div className="chart-container">
                {trends.length > 0 ? (
                  <div className="simple-chart">
                    {trends.map((trend, index) => (
                      <div key={index} className="chart-bar-group">
                        <div className="chart-bars">
                          <div 
                            className="chart-bar users"
                            style={{ height: `${(trend.users / Math.max(...trends.map(t => t.users))) * 100}%` }}
                            title={`Users: ${trend.users}`}
                          ></div>
                          <div 
                            className="chart-bar matches"
                            style={{ height: `${(trend.matches / Math.max(...trends.map(t => t.matches))) * 100}%` }}
                            title={`Matches: ${trend.matches}`}
                          ></div>
                          <div 
                            className="chart-bar meetings"
                            style={{ height: `${(trend.meetings / Math.max(...trends.map(t => t.meetings))) * 100}%` }}
                            title={`Meetings: ${trend.meetings}`}
                          ></div>
                          <div 
                            className="chart-bar messages"
                            style={{ height: `${(trend.messages / Math.max(...trends.map(t => t.messages))) * 100}%` }}
                            title={`Messages: ${trend.messages}`}
                          ></div>
                        </div>
                        <div className="chart-label">
                          {new Date(trend.date).toLocaleDateString('en-US', { 
                            month: 'short', 
                            day: 'numeric' 
                          })}
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="no-data">
                    <p>No trend data available for the selected period.</p>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Quick Stats */}
          <div className="quick-stats">
            <div className="stat-item">
              <span className="stat-label">Active Users</span>
              <span className="stat-value">{formatNumber(overview.activeUsers)}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Successful Matches</span>
              <span className="stat-value">{formatNumber(overview.successfulMatches)}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Completed Meetings</span>
              <span className="stat-value">{formatNumber(overview.completedMeetings)}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Average Rating</span>
              <span className="stat-value">{overview.averageRating.toFixed(1)}⭐</span>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default AnalyticsDashboard;
