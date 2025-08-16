import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import "./Navbar.css";

const Navbar: React.FC = () => {
    const { user, isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/" className="navbar-logo">
                    💧 Water Cooler Network
                </Link>
            </div>

            <div className="navbar-menu">
                {isAuthenticated ? (
                    <>
                        <Link to="/dashboard" className="navbar-item">
                            Dashboard
                        </Link>
                        <Link to="/matches" className="navbar-item">
                            Find Matches
                        </Link>
                        <Link to="/my-matches" className="navbar-item">
                            My Matches
                        </Link>
                        <Link to="/lounges" className="nav-link">
                            Topic Lounges
                        </Link>
                        <Link to="/achievements" className="nav-link">
                            🏆 Achievements
                        </Link>
                        <Link to="/corporate" className="nav-link">
                            🏢 Corporate
                        </Link>
                        <Link to="/smart-matching" className="nav-link">
                            Smart Matching
                        </Link>
                        <Link to="/meetings" className="nav-link">
                            Meetings
                        </Link>
                        <Link to="/feedback" className="nav-link">
                            Feedback
                        </Link>
                        <Link to="/analytics" className="nav-link">
                            Analytics
                        </Link>
                        <Link to="/insights" className="nav-link">
                            My Insights
                        </Link>
                        <Link to="/profile" className="navbar-item">
                            Profile
                        </Link>
                        <div className="navbar-user">
                            <span>Welcome, {user?.name}</span>
                            <button
                                onClick={handleLogout}
                                className="logout-btn"
                            >
                                Logout
                            </button>
                        </div>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="navbar-item">
                            Login
                        </Link>
                        <Link to="/register" className="navbar-item">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
