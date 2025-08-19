import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './UserInsightsDashboard.css';

interface UserInsight {
    id: number;
    title: string;
    description: string;
    recommendation: string;
    insightType: string;
    insightTypeDisplay: string;
    confidenceScore: number;
    priorityLevel: number;
    category: string;
    tags: string;
    isRead: boolean;
    isActioned: boolean;
    feedbackRating: number;
    feedbackComment: string;
    createdAt: string;
    statusDisplay: string;
    confidenceDisplay: string;
    priorityDisplay: string;
}

interface BehaviorAnalysis {
    totalActivities: number;
    recentActivities: number;
    behaviorDistribution: Record<string, number>;
    engagementScore: number;
    recommendations: string[];
}

const UserInsightsDashboard: React.FC = () => {
    const [activeTab, setActiveTab] = useState<'insights' | 'analysis' | 'behavior'>('insights');
    const [insights, setInsights] = useState<UserInsight[]>([]);
    const [behaviorAnalysis, setBehaviorAnalysis] = useState<BehaviorAnalysis | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentUserId, setCurrentUserId] = useState<number>(1); // Default user ID
    const [selectedInsightType, setSelectedInsightType] = useState<string>('all');

    useEffect(() => {
        fetchData();
    }, [activeTab, currentUserId]);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            switch (activeTab) {
                case 'insights':
                    const insightsResponse = await axios.get(`/api/user-insights/user/${currentUserId}`);
                    setInsights(insightsResponse.data);
                    break;
                case 'analysis':
                    const analysisResponse = await axios.get(`/api/user-insights/analysis/${currentUserId}`);
                    setBehaviorAnalysis(analysisResponse.data);
                    break;
                case 'behavior':
                    // Fetch behavior data
                    break;
            }
        } catch (err) {
            setError('Failed to fetch data');
            console.error('Error fetching data:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleTabChange = (tab: 'insights' | 'analysis' | 'behavior') => {
        setActiveTab(tab);
    };

    const handleInsightTypeFilter = (insightType: string) => {
        setSelectedInsightType(insightType);
    };

    const handleMarkAsRead = async (insightId: number) => {
        try {
            await axios.put(`/api/user-insights/${insightId}/read`);
            // Update local state
            setInsights(prev => prev.map(insight => 
                insight.id === insightId ? { ...insight, isRead: true } : insight
            ));
        } catch (err) {
            console.error('Error marking insight as read:', err);
        }
    };

    const handleMarkAsActioned = async (insightId: number) => {
        try {
            await axios.put(`/api/user-insights/${insightId}/actioned`);
            // Update local state
            setInsights(prev => prev.map(insight => 
                insight.id === insightId ? { ...insight, isActioned: true } : insight
            ));
        } catch (err) {
            console.error('Error marking insight as actioned:', err);
        }
    };

    const handleGenerateInsights = async () => {
        try {
            setLoading(true);
            const response = await axios.post(`/api/user-insights/generate/${currentUserId}`);
            setInsights(response.data);
            setActiveTab('insights');
        } catch (err) {
            setError('Failed to generate insights');
            console.error('Error generating insights:', err);
        } finally {
            setLoading(false);
        }
    };

    const filteredInsights = selectedInsightType === 'all' 
        ? insights 
        : insights.filter(insight => insight.insightType === selectedInsightType);

    if (loading) {
        return (
            <div className="user-insights-dashboard">
                <div className="loading-spinner">Loading insights...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="user-insights-dashboard">
                <div className="error-message">{error}</div>
                <button onClick={fetchData} className="retry-button">
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="user-insights-dashboard">
            <div className="dashboard-header">
                <h1>AI-Powered User Insights</h1>
                <div className="dashboard-controls">
                    <button className="generate-insights-button" onClick={handleGenerateInsights}>
                        🔮 Generate New Insights
                    </button>
                </div>
            </div>

            <div className="tab-navigation">
                <button
                    className={`tab-button ${activeTab === 'insights' ? 'active' : ''}`}
                    onClick={() => handleTabChange('insights')}
                >
                    💡 Insights & Recommendations
                </button>
                <button
                    className={`tab-button ${activeTab === 'analysis' ? 'active' : ''}`}
                    onClick={() => handleTabChange('analysis')}
                >
                    📊 Behavioral Analysis
                </button>
                <button
                    className={`tab-button ${activeTab === 'behavior' ? 'active' : ''}`}
                    onClick={() => handleTabChange('behavior')}
                >
                    🎯 Behavior Tracking
                </button>
            </div>

            <div className="tab-content">
                {activeTab === 'insights' && (
                    <div className="insights-section">
                        <div className="insights-header">
                            <h2>Your Personalized Insights</h2>
                            <div className="insight-filters">
                                <button
                                    className={`filter-button ${selectedInsightType === 'all' ? 'active' : ''}`}
                                    onClick={() => handleInsightTypeFilter('all')}
                                >
                                    All
                                </button>
                                <button
                                    className={`filter-button ${selectedInsightType === 'MATCHING_IMPROVEMENT' ? 'active' : ''}`}
                                    onClick={() => handleInsightTypeFilter('MATCHING_IMPROVEMENT')}
                                >
                                    Matching
                                </button>
                                <button
                                    className={`filter-button ${selectedInsightType === 'SKILL_DEVELOPMENT' ? 'active' : ''}`}
                                    onClick={() => handleInsightTypeFilter('SKILL_DEVELOPMENT')}
                                >
                                    Skills
                                </button>
                                <button
                                    className={`filter-button ${selectedInsightType === 'NETWORKING_OPPORTUNITY' ? 'active' : ''}`}
                                    onClick={() => handleInsightTypeFilter('NETWORKING_OPPORTUNITY')}
                                >
                                    Networking
                                </button>
                            </div>
                        </div>

                        <div className="insights-grid">
                            {filteredInsights.map((insight) => (
                                <div key={insight.id} className={`insight-card ${insight.isRead ? 'read' : 'unread'}`}>
                                    <div className="insight-header">
                                        <div className="insight-type-badge">
                                            {insight.insightTypeDisplay}
                                        </div>
                                        <div className="insight-status">
                                            <span className={`status-badge ${insight.statusDisplay.toLowerCase()}`}>
                                                {insight.statusDisplay}
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <h3 className="insight-title">{insight.title}</h3>
                                    <p className="insight-description">{insight.description}</p>
                                    
                                    <div className="insight-recommendation">
                                        <h4>💡 Recommendation:</h4>
                                        <p>{insight.recommendation}</p>
                                    </div>
                                    
                                    <div className="insight-metrics">
                                        <div className="metric">
                                            <span className="metric-label">Confidence:</span>
                                            <span className={`metric-value confidence-${insight.confidenceDisplay.toLowerCase()}`}>
                                                {insight.confidenceDisplay}
                                            </span>
                                        </div>
                                        <div className="metric">
                                            <span className="metric-label">Priority:</span>
                                            <span className={`metric-value priority-${insight.priorityDisplay.toLowerCase()}`}>
                                                {insight.priorityDisplay}
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div className="insight-actions">
                                        {!insight.isRead && (
                                            <button
                                                className="action-button read-button"
                                                onClick={() => handleMarkAsRead(insight.id)}
                                            >
                                                Mark as Read
                                            </button>
                                        )}
                                        {!insight.isActioned && (
                                            <button
                                                className="action-button action-button-primary"
                                                onClick={() => handleMarkAsActioned(insight.id)}
                                            >
                                                Mark as Actioned
                                            </button>
                                        )}
                                    </div>
                                    
                                    <div className="insight-footer">
                                        <span className="insight-date">
                                            {new Date(insight.createdAt).toLocaleDateString()}
                                        </span>
                                        {insight.tags && (
                                            <div className="insight-tags">
                                                {insight.tags.split(',').map((tag, index) => (
                                                    <span key={index} className="tag">
                                                        {tag.trim()}
                                                    </span>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'analysis' && behaviorAnalysis && (
                    <div className="analysis-section">
                        <h2>Behavioral Analysis</h2>
                        
                        <div className="analysis-overview">
                            <div className="analysis-card">
                                <h3>Activity Summary</h3>
                                <div className="analysis-metrics">
                                    <div className="metric-item">
                                        <span className="metric-value">{behaviorAnalysis.totalActivities}</span>
                                        <span className="metric-label">Total Activities</span>
                                    </div>
                                    <div className="metric-item">
                                        <span className="metric-value">{behaviorAnalysis.recentActivities}</span>
                                        <span className="metric-label">Recent (7 days)</span>
                                    </div>
                                    <div className="metric-item">
                                        <span className="metric-value">{Math.round(behaviorAnalysis.engagementScore)}</span>
                                        <span className="metric-label">Engagement Score</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="behavior-distribution">
                            <h3>Behavior Distribution</h3>
                            <div className="distribution-chart">
                                {Object.entries(behaviorAnalysis.behaviorDistribution).map(([behavior, count]) => (
                                    <div key={behavior} className="behavior-bar">
                                        <div className="behavior-label">{behavior.replace(/_/g, ' ')}</div>
                                        <div className="behavior-bar-container">
                                            <div 
                                                className="behavior-bar-fill"
                                                style={{ width: `${(count / Math.max(...Object.values(behaviorAnalysis.behaviorDistribution))) * 100}%` }}
                                            ></div>
                                        </div>
                                        <div className="behavior-count">{count}</div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="recommendations-section">
                            <h3>AI Recommendations</h3>
                            <div className="recommendations-list">
                                {behaviorAnalysis.recommendations.map((recommendation, index) => (
                                    <div key={index} className="recommendation-item">
                                        <span className="recommendation-icon">💡</span>
                                        <span className="recommendation-text">{recommendation}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                )}

                {activeTab === 'behavior' && (
                    <div className="behavior-section">
                        <h2>Behavior Tracking</h2>
                        <div className="behavior-info">
                            <p>Your behavior is automatically tracked to provide personalized insights and recommendations.</p>
                            <p>This includes:</p>
                            <ul>
                                <li>Platform interactions (logins, profile updates)</li>
                                <li>Matching activities (requests, accepts, rejects)</li>
                                <li>Networking behaviors (coffee chats, lounge participation)</li>
                                <li>Skill development (badges, mentorship)</li>
                                <li>Engagement patterns (feedback, ratings)</li>
                            </ul>
                            <p>All data is processed securely and used only to improve your experience.</p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default UserInsightsDashboard;
