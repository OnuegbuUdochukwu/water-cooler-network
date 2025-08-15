import React from "react";
import { useAuth } from "../../contexts/AuthContext";
import "./Dashboard.css";

const Dashboard: React.FC = () => {
    const { user } = useAuth();

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

                <div className="features-overview card">
                    <div className="card-header">
                        <h2 className="card-title">🚀 Coming Soon</h2>
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
                        <button className="btn btn-secondary">
                            Browse Users
                        </button>
                        <button className="btn btn-secondary">
                            View Analytics
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
