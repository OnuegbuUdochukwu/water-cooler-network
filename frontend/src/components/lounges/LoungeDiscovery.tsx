import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../../contexts/AuthContext";
import { Link } from "react-router-dom";
import "./Lounges.css";

interface Lounge {
    id: number;
    title: string;
    description: string;
    topic: string;
    category: string;
    tags: string;
    creatorName: string;
    visibility: string;
    maxParticipants: number;
    currentParticipants: number;
    isActive: boolean;
    isFeatured: boolean;
    lastActivity: string;
    createdAt: string;
    isParticipant: boolean;
    participantRole: string;
}

const LoungeDiscovery: React.FC = () => {
    const { token } = useAuth();
    const [lounges, setLounges] = useState<Lounge[]>([]);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("all");
    const [selectedTopic, setSelectedTopic] = useState("all");
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetchLounges();
    }, []);

    const fetchLounges = async () => {
        setLoading(true);
        try {
            const response = await axios.get("/api/lounges", {
                headers: { Authorization: `Bearer ${token}` },
            });
            setLounges(response.data);
        } catch (error) {
            setMessage("Failed to fetch lounges");
        } finally {
            setLoading(false);
        }
    };

    const searchLounges = async () => {
        if (!searchTerm.trim()) {
            fetchLounges();
            return;
        }

        setLoading(true);
        try {
            const response = await axios.get(
                `/api/lounges/search?q=${searchTerm}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setLounges(response.data);
        } catch (error) {
            setMessage("Search failed");
        } finally {
            setLoading(false);
        }
    };

    const filterByCategory = async (category: string) => {
        setSelectedCategory(category);
        if (category === "all") {
            fetchLounges();
            return;
        }

        setLoading(true);
        try {
            const response = await axios.get(
                `/api/lounges/category/${category}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setLounges(response.data);
        } catch (error) {
            setMessage("Failed to filter by category");
        } finally {
            setLoading(false);
        }
    };

    const filterByTopic = async (topic: string) => {
        setSelectedTopic(topic);
        if (topic === "all") {
            fetchLounges();
            return;
        }

        setLoading(true);
        try {
            const response = await axios.get(`/api/lounges/topic/${topic}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setLounges(response.data);
        } catch (error) {
            setMessage("Failed to filter by topic");
        } finally {
            setLoading(false);
        }
    };

    const joinLounge = async (loungeId: number) => {
        try {
            await axios.post(
                `/api/lounges/${loungeId}/join`,
                {},
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setMessage("Successfully joined the lounge!");
            fetchLounges(); // Refresh the list
        } catch (error) {
            setMessage("Failed to join lounge");
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getTagList = (tags: string) => {
        if (!tags) return [];
        return tags.split(",").map((tag) => tag.trim());
    };

    const getVisibilityIcon = (visibility: string) => {
        switch (visibility) {
            case "PUBLIC":
                return "🌍";
            case "PRIVATE":
                return "🔒";
            case "CORPORATE":
                return "🏢";
            default:
                return "🌍";
        }
    };

    if (loading) {
        return (
            <div className="lounge-discovery">
                <div className="loading-spinner">
                    <div className="spinner"></div>
                    <p>Discovering amazing topic lounges...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="lounge-discovery">
            <div className="discovery-header">
                <h1>💬 Topic Lounges</h1>
                <p>Join conversations around your professional interests</p>
            </div>

            {message && (
                <div
                    className={`message ${
                        message.includes("Successfully")
                            ? "success-message"
                            : "error-message"
                    }`}
                >
                    {message}
                </div>
            )}

            <div className="discovery-controls">
                <div className="search-section">
                    <input
                        type="text"
                        placeholder="Search lounges by title, topic, or description..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                    <button onClick={searchLounges} className="btn btn-primary">
                        Search
                    </button>
                </div>

                <div className="filter-section">
                    <select
                        value={selectedCategory}
                        onChange={(e) => filterByCategory(e.target.value)}
                        className="filter-select"
                    >
                        <option value="all">All Categories</option>
                        <option value="Technology">Technology</option>
                        <option value="Healthcare">Healthcare</option>
                        <option value="Finance">Finance</option>
                        <option value="Education">Education</option>
                        <option value="Marketing">Marketing</option>
                        <option value="Design">Design</option>
                        <option value="Sales">Sales</option>
                        <option value="Operations">Operations</option>
                    </select>

                    <select
                        value={selectedTopic}
                        onChange={(e) => filterByTopic(e.target.value)}
                        className="filter-select"
                    >
                        <option value="all">All Topics</option>
                        <option value="AI">AI & Machine Learning</option>
                        <option value="Remote Work">Remote Work</option>
                        <option value="Leadership">Leadership</option>
                        <option value="Career Growth">Career Growth</option>
                        <option value="Innovation">Innovation</option>
                        <option value="Networking">Networking</option>
                        <option value="Industry Trends">Industry Trends</option>
                    </select>

                    <button
                        onClick={fetchLounges}
                        className="btn btn-secondary"
                    >
                        Clear Filters
                    </button>
                </div>
            </div>

            <div className="lounges-grid">
                {lounges.length === 0 ? (
                    <div className="no-lounges">
                        <h3>No lounges found</h3>
                        <p>
                            Try adjusting your search or filters to discover
                            more lounges!
                        </p>
                        <Link to="/lounges/create" className="btn btn-primary">
                            Create Your First Lounge
                        </Link>
                    </div>
                ) : (
                    lounges.map((lounge) => (
                        <div key={lounge.id} className="lounge-card">
                            <div className="lounge-header">
                                <div className="lounge-title">
                                    <h3>{lounge.title}</h3>
                                    {lounge.isFeatured && (
                                        <span className="featured-badge">
                                            ⭐ Featured
                                        </span>
                                    )}
                                </div>
                                <div className="lounge-meta">
                                    <span className="visibility-icon">
                                        {getVisibilityIcon(lounge.visibility)}
                                    </span>
                                    <span className="participant-count">
                                        {lounge.currentParticipants}
                                        {lounge.maxParticipants &&
                                            `/${lounge.maxParticipants}`}
                                    </span>
                                </div>
                            </div>

                            <div className="lounge-content">
                                <p className="lounge-description">
                                    {lounge.description}
                                </p>

                                <div className="lounge-tags">
                                    <span className="tag topic-tag">
                                        {lounge.topic}
                                    </span>
                                    {lounge.category && (
                                        <span className="tag category-tag">
                                            {lounge.category}
                                        </span>
                                    )}
                                    {getTagList(lounge.tags).map(
                                        (tag, index) => (
                                            <span key={index} className="tag">
                                                {tag}
                                            </span>
                                        )
                                    )}
                                </div>

                                <div className="lounge-info">
                                    <p>
                                        <strong>Created by:</strong>{" "}
                                        {lounge.creatorName}
                                    </p>
                                    <p>
                                        <strong>Last activity:</strong>{" "}
                                        {formatDate(lounge.lastActivity)}
                                    </p>
                                    <p>
                                        <strong>Created:</strong>{" "}
                                        {formatDate(lounge.createdAt)}
                                    </p>
                                </div>
                            </div>

                            <div className="lounge-actions">
                                {lounge.isParticipant ? (
                                    <div className="participant-status">
                                        <span className="status-badge">
                                            ✓ Joined
                                        </span>
                                        <Link
                                            to={`/lounges/${lounge.id}`}
                                            className="btn btn-primary"
                                        >
                                            Enter Lounge
                                        </Link>
                                    </div>
                                ) : (
                                    <button
                                        onClick={() => joinLounge(lounge.id)}
                                        className="btn btn-primary"
                                        disabled={
                                            lounge.currentParticipants >=
                                            (lounge.maxParticipants || 999)
                                        }
                                    >
                                        {lounge.currentParticipants >=
                                        (lounge.maxParticipants || 999)
                                            ? "Lounge Full"
                                            : "Join Lounge"}
                                    </button>
                                )}

                                <Link
                                    to={`/lounges/${lounge.id}`}
                                    className="btn btn-secondary"
                                >
                                    View Details
                                </Link>
                            </div>
                        </div>
                    ))
                )}
            </div>

            <div className="discovery-footer">
                <Link to="/lounges/create" className="btn btn-primary">
                    Create New Lounge
                </Link>
                <button onClick={fetchLounges} className="btn btn-secondary">
                    Refresh Lounges
                </button>
            </div>
        </div>
    );
};

export default LoungeDiscovery;
