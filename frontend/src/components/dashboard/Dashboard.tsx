import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import BadgeDisplay from "../gamification/BadgeDisplay";
import StreakCounter from "../gamification/StreakCounter";
import axios from "axios";
import "./Dashboard.css";

const Dashboard: React.FC = () => {
    const { user } = useAuth();
    const [gamificationSummary, setGamificationSummary] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchGamificationSummary();
    }, []);

    const fetchGamificationSummary = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('/api/gamification/summary', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setGamificationSummary(response.data);
        } catch (error) {
            console.error('Error fetching gamification summary:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="dashboard">
            <div className="dashboard-header">
                <h1>Welcome to Water Cooler Network</h1>
                <p>Your professional networking hub</p>
            </div>

            <div className="dashboard-content">
                <div className="welcome-card card">
                    <div className="card-header">
                        <h2 className="card-title">
                            👋 Welcome, {user?.name}!
                        </h2>
                    </div>
                    <div className="user-info">
                        <p>
                            <strong>Email:</strong> {user?.email}
                        </p>
                        <p>
                            <strong>Role:</strong> {user?.role}
                        </p>
                        {user?.industry && (
                            <p>
                                <strong>Industry:</strong> {user?.industry}
                            </p>
                        )}
                        {user?.companyId && (
                            <p>
                                <strong>Company ID:</strong> {user?.companyId}
                            </p>
                        )}
                    </div>
                </div>

                {/* Gamification Summary */}
                {!loading && gamificationSummary && (
                    <div className="gamification-summary card">
                        <div className="card-header">
                            <h2 className="card-title">🏆 Your Progress</h2>
                            <Link to="/achievements" className="view-all-link">
                                View All →
                            </Link>
                        </div>
                        <div className="gamification-grid">
                            <div className="gamification-stats">
                                <div className="stat-item">
                                    <span className="stat-number">{gamificationSummary.totalBadges}</span>
                                    <span className="stat-label">Badges</span>
                                </div>
                                <div className="stat-item">
                                    <span className="stat-number">{gamificationSummary.totalPoints}</span>
                                    <span className="stat-label">Points</span>
                                </div>
                                <div className="stat-item">
                                    <span className="stat-number">{gamificationSummary.longestStreak}</span>
                                    <span className="stat-label">Best Streak</span>
                                </div>
                            </div>
                            {gamificationSummary.activeStreaks && gamificationSummary.activeStreaks.length > 0 && (
                                <div className="dashboard-streaks">
                                    <StreakCounter streaks={gamificationSummary.activeStreaks} compact />
                                </div>
                            )}
                            {gamificationSummary.displayedBadges && gamificationSummary.displayedBadges.length > 0 && (
                                <div className="dashboard-badges">
                                    <BadgeDisplay 
                                        badges={gamificationSummary.displayedBadges} 
                                        maxDisplay={4}
                                        size="small"
                                    />
                                </div>
                            )}
                        </div>
                    </div>
                )}

                <div className="features-overview card">
                    <div className="card-header">
                        <h2 className="card-title">🚀 Platform Features</h2>
                    </div>
                    <div className="features-grid">
                        <div className="feature-item">
                            <h3>☕ Coffee Chat Matching</h3>
                            <p>
                                Get matched with professionals for instant video
                                chats
                            </p>
                        </div>
                        <div className="feature-item">
                            <h3>💬 Topic Lounges</h3>
                            <p>Join conversations in your areas of interest</p>
                        </div>
                        <div className="feature-item">
                            <h3>🏆 Gamification</h3>
                            <p>Earn badges and streaks for active networking</p>
                        </div>
                        <div className="feature-item">
                            <h3>🏢 Corporate Spaces</h3>
                            <p>Private workspaces for your organization</p>
                        </div>
                    </div>
                </div>

                <div className="quick-actions card">
                    <div className="card-header">
                        <h2 className="card-title">⚡ Quick Actions</h2>
                    </div>
                    <div className="actions-grid">
                        <button className="btn btn-primary">
                            Update Profile
                        </button>
                        <Link to="/matches" className="btn btn-secondary">
                            Find Matches
                        </Link>
                        <Link to="/my-matches" className="btn btn-secondary">
                            My Matches
                        </Link>
                        <Link to="/lounges" className="btn btn-secondary">
                            Browse Lounges
                        </Link>
                        <Link to="/lounges/create" className="btn btn-primary">
                            Create Lounge
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
