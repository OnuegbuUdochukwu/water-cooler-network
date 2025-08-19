import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './AnalyticsDashboard.css';

interface AnalyticsData {
    dailyActiveUsers: number;
    weeklyActiveUsers: number;
    monthlyActiveUsers: number;
    totalConversations: number;
    totalVideoCalls: number;
    averageSessionDuration: number;
    badgesEarned: number;
    streaksMaintained: number;
    userGrowth: number;
    engagementGrowth: number;
}

interface MetricCardProps {
    title: string;
    value: string | number;
    change: number;
    icon: string;
}

const MetricCard: React.FC<MetricCardProps> = ({ title, value, change, icon }) => (
    <div className="metric-card">
        <div className="metric-icon">{icon}</div>
        <div className="metric-content">
            <h3 className="metric-title">{title}</h3>
            <div className="metric-value">{value}</div>
            <div className={`metric-change ${change >= 0 ? 'positive' : 'negative'}`}>
                {change >= 0 ? '↗' : '↘'} {Math.abs(change)}%
            </div>
        </div>
    </div>
);

const AnalyticsDashboard: React.FC = () => {
    const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedPeriod, setSelectedPeriod] = useState<'daily' | 'weekly' | 'monthly'>('daily');
    const [selectedCompany, setSelectedCompany] = useState<number>(1); // Default company ID

    useEffect(() => {
        fetchAnalyticsData();
    }, [selectedCompany, selectedPeriod]);

    const fetchAnalyticsData = async () => {
        try {
            setLoading(true);
            const response = await axios.get(`/api/analytics/company/${selectedCompany}`);
            setAnalyticsData(response.data);
            setError(null);
        } catch (err) {
            setError('Failed to fetch analytics data');
            console.error('Error fetching analytics:', err);
        } finally {
            setLoading(false);
        }
    };

    const handlePeriodChange = (period: 'daily' | 'weekly' | 'monthly') => {
        setSelectedPeriod(period);
    };

    const handleCompanyChange = (companyId: number) => {
        setSelectedCompany(companyId);
    };

    if (loading) {
        return (
            <div className="analytics-dashboard">
                <div className="loading-spinner">Loading analytics...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="analytics-dashboard">
                <div className="error-message">{error}</div>
                <button onClick={fetchAnalyticsData} className="retry-button">
                    Retry
                </button>
            </div>
        );
    }

    if (!analyticsData) {
        return (
            <div className="analytics-dashboard">
                <div className="no-data">No analytics data available</div>
            </div>
        );
    }

    return (
        <div className="analytics-dashboard">
            <div className="dashboard-header">
                <h1>HR Analytics Dashboard</h1>
                <div className="dashboard-controls">
                    <div className="period-selector">
                        <button
                            className={selectedPeriod === 'daily' ? 'active' : ''}
                            onClick={() => handlePeriodChange('daily')}
                        >
                            Daily
                        </button>
                        <button
                            className={selectedPeriod === 'weekly' ? 'active' : ''}
                            onClick={() => handlePeriodChange('weekly')}
                        >
                            Weekly
                        </button>
                        <button
                            className={selectedPeriod === 'monthly' ? 'active' : ''}
                            onClick={() => handlePeriodChange('monthly')}
                        >
                            Monthly
                        </button>
                    </div>
                    <div className="company-selector">
                        <label htmlFor="company-select">Company:</label>
                        <select
                            id="company-select"
                            value={selectedCompany}
                            onChange={(e) => handleCompanyChange(Number(e.target.value))}
                        >
                            <option value={1}>Company 1</option>
                            <option value={2}>Company 2</option>
                            <option value={3}>Company 3</option>
                        </select>
                    </div>
                </div>
            </div>

            <div className="metrics-grid">
                <MetricCard
                    title="Daily Active Users"
                    value={analyticsData.dailyActiveUsers}
                    change={analyticsData.userGrowth}
                    icon="👥"
                />
                <MetricCard
                    title="Weekly Active Users"
                    value={analyticsData.weeklyActiveUsers}
                    change={analyticsData.userGrowth}
                    icon="📊"
                />
                <MetricCard
                    title="Monthly Active Users"
                    value={analyticsData.monthlyActiveUsers}
                    change={analyticsData.userGrowth}
                    icon="📈"
                />
                <MetricCard
                    title="Total Conversations"
                    value={analyticsData.totalConversations}
                    change={analyticsData.engagementGrowth}
                    icon="💬"
                />
                <MetricCard
                    title="Video Calls"
                    value={analyticsData.totalVideoCalls}
                    change={analyticsData.engagementGrowth}
                    icon="📹"
                />
                <MetricCard
                    title="Avg Session Duration"
                    value={`${analyticsData.averageSessionDuration.toFixed(1)} min`}
                    change={analyticsData.engagementGrowth}
                    icon="⏱️"
                />
                <MetricCard
                    title="Badges Earned"
                    value={analyticsData.badgesEarned}
                    change={analyticsData.engagementGrowth}
                    icon="🏆"
                />
                <MetricCard
                    title="Streaks Maintained"
                    value={analyticsData.streaksMaintained}
                    change={analyticsData.engagementGrowth}
                    icon="🔥"
                />
            </div>

            <div className="charts-section">
                <div className="chart-container">
                    <h3>User Engagement Trends</h3>
                    <div className="chart-placeholder">
                        Chart visualization coming soon...
                    </div>
                </div>
                <div className="chart-container">
                    <h3>Department Performance</h3>
                    <div className="chart-placeholder">
                        Chart visualization coming soon...
                    </div>
                </div>
            </div>

            <div className="insights-section">
                <h3>Key Insights</h3>
                <div className="insights-grid">
                    <div className="insight-card">
                        <h4>User Growth</h4>
                        <p>User base has grown by {analyticsData.userGrowth}% this period</p>
                    </div>
                    <div className="insight-card">
                        <h4>Engagement</h4>
                        <p>Overall engagement increased by {analyticsData.engagementGrowth}%</p>
                    </div>
                    <div className="insight-card">
                        <h4>Activity</h4>
                        <p>Average daily active users: {analyticsData.dailyActiveUsers}</p>
                    </div>
                </div>
            </div>

            <div className="actions-section">
                <button className="export-button" onClick={() => alert('Export functionality coming soon')}>
                    Export Report
                </button>
                <button className="refresh-button" onClick={fetchAnalyticsData}>
                    Refresh Data
                </button>
            </div>
        </div>
    );
};

export default AnalyticsDashboard;
