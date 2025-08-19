import React, { useState, useEffect } from 'react';
import axios from 'axios';
import BadgeDisplay from './BadgeDisplay';
import StreakCounter from './StreakCounter';
import AchievementNotification from './AchievementNotification';
import Leaderboard from './Leaderboard';
import './AchievementsDashboard.css';

interface GamificationSummary {
    userId: number;
    activeStreaks: any[];
    recentBadges: any[];
    totalBadges: number;
    totalPoints: number;
    longestStreak: number;
    longestStreakType: string;
    displayedBadges: any[];
    hasNewAchievements: boolean;
}

const AchievementsDashboard: React.FC = () => {
    const [summary, setSummary] = useState<GamificationSummary | null>(null);
    const [allBadges, setAllBadges] = useState<any[]>([]);
    const [allStreaks, setAllStreaks] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [newAchievement, setNewAchievement] = useState<any>(null);
    const [activeTab, setActiveTab] = useState<'overview' | 'badges' | 'streaks' | 'leaderboard'>('overview');

    useEffect(() => {
        fetchGamificationData();
    }, []);

    const fetchGamificationData = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem('token');
            const headers = { Authorization: `Bearer ${token}` };

            const [summaryRes, badgesRes, streaksRes] = await Promise.all([
                axios.get('/api/gamification/summary', { headers }),
                axios.get('/api/gamification/badges', { headers }),
                axios.get('/api/gamification/streaks', { headers })
            ]);

            setSummary(summaryRes.data);
            setAllBadges(badgesRes.data);
            setAllStreaks(streaksRes.data);

            // Check for new achievements
            if (summaryRes.data.hasNewAchievements && summaryRes.data.recentBadges.length > 0) {
                setNewAchievement(summaryRes.data.recentBadges[0]);
            }
        } catch (err) {
            setError('Failed to load achievements data');
            console.error('Error fetching gamification data:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleCloseNotification = () => {
        setNewAchievement(null);
    };

    if (loading) {
        return (
            <div className="achievements-dashboard loading">
                <div className="loading-spinner">
                    <div className="spinner"></div>
                    <p>Loading your achievements...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="achievements-dashboard error">
                <div className="error-message">
                    <span className="error-icon">⚠️</span>
                    <p>{error}</p>
                    <button onClick={fetchGamificationData} className="retry-button">
                        Try Again
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="achievements-dashboard">
            <div className="dashboard-header">
                <h1>🏆 Your Achievements</h1>
                <div className="stats-summary">
                    <div className="stat-item">
                        <span className="stat-number">{summary?.totalBadges || 0}</span>
                        <span className="stat-label">Badges Earned</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-number">{summary?.totalPoints || 0}</span>
                        <span className="stat-label">Total Points</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-number">{summary?.longestStreak || 0}</span>
                        <span className="stat-label">Longest Streak</span>
                    </div>
                </div>
            </div>

            <div className="dashboard-tabs">
                <button 
                    className={`tab-button ${activeTab === 'overview' ? 'active' : ''}`}
                    onClick={() => setActiveTab('overview')}
                >
                    📊 Overview
                </button>
                <button 
                    className={`tab-button ${activeTab === 'badges' ? 'active' : ''}`}
                    onClick={() => setActiveTab('badges')}
                >
                    🏅 Badges ({allBadges.length})
                </button>
                <button 
                    className={`tab-button ${activeTab === 'streaks' ? 'active' : ''}`}
                    onClick={() => setActiveTab('streaks')}
                >
                    🔥 Streaks ({allStreaks.filter(s => s.isActive).length})
                </button>
                <button 
                    className={`tab-button ${activeTab === 'leaderboard' ? 'active' : ''}`}
                    onClick={() => setActiveTab('leaderboard')}
                >
                    🏆 Leaderboard
                </button>
            </div>

            <div className="dashboard-content">
                {activeTab === 'overview' && (
                    <div className="overview-tab">
                        <div className="overview-grid">
                            <div className="overview-section">
                                <h3>🔥 Active Streaks</h3>
                                <StreakCounter streaks={summary?.activeStreaks || []} compact />
                            </div>
                            
                            <div className="overview-section">
                                <h3>🏅 Recent Badges</h3>
                                <BadgeDisplay 
                                    badges={summary?.recentBadges || []} 
                                    maxDisplay={6}
                                    size="medium"
                                />
                            </div>
                            
                            <div className="overview-section">
                                <h3>⭐ Displayed Badges</h3>
                                <BadgeDisplay 
                                    badges={summary?.displayedBadges || []} 
                                    maxDisplay={8}
                                    size="small"
                                />
                            </div>
                        </div>
                    </div>
                )}

                {activeTab === 'badges' && (
                    <div className="badges-tab">
                        <div className="tab-header">
                            <h3>All Your Badges</h3>
                            <p>Collect badges by completing various activities on the platform</p>
                        </div>
                        <BadgeDisplay 
                            badges={allBadges} 
                            showProgress={true}
                            size="large"
                        />
                        {allBadges.length === 0 && (
                            <div className="empty-state">
                                <span className="empty-icon">🎯</span>
                                <h4>No badges yet!</h4>
                                <p>Start participating in coffee chats and lounges to earn your first badges.</p>
                            </div>
                        )}
                    </div>
                )}

                {activeTab === 'streaks' && (
                    <div className="streaks-tab">
                        <div className="tab-header">
                            <h3>Your Streaks</h3>
                            <p>Build streaks by staying consistent with your activities</p>
                        </div>
                        <StreakCounter streaks={allStreaks} />
                    </div>
                )}
                
                {activeTab === 'leaderboard' && (
                    <div className="leaderboard-tab">
                        <div className="tab-header">
                            <h3>Global Leaderboard</h3>
                            <p>See how you rank against other users</p>
                        </div>
                        <Leaderboard limit={20} showUserRank={true} />
                    </div>
                )}
            </div>

            <AchievementNotification 
                achievement={newAchievement}
                onClose={handleCloseNotification}
            />
        </div>
    );
};

export default AchievementsDashboard;
