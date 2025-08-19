import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../../contexts/AuthContext";
import "./Matching.css";

interface AvailableMatch {
    user1Id: number;
    user2Id: number;
    user2Name: string;
    user2Email: string;
    compatibilityScore: number;
    matchType: string;
    status: string;
}

const MatchFinder: React.FC = () => {
    const { token } = useAuth();
    const [availableMatches, setAvailableMatches] = useState<AvailableMatch[]>(
        []
    );
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetchAvailableMatches();
    }, []);

    const fetchAvailableMatches = async () => {
        setLoading(true);
        try {
            const response = await axios.get("/api/matches/available", {
                headers: { Authorization: `Bearer ${token}` },
            });
            setAvailableMatches(response.data);
        } catch (error) {
            setMessage("Failed to fetch available matches");
        } finally {
            setLoading(false);
        }
    };

    const sendMatchRequest = async (targetUserId: number) => {
        try {
            const matchRequest = {
                targetUserId,
                matchType: "COFFEE_CHAT",
                message: "I'd love to have a coffee chat with you!",
                preferredTime: new Date(
                    Date.now() + 24 * 60 * 60 * 1000
                ).toISOString(), // Tomorrow
                durationMinutes: 30,
            };

            await axios.post("/api/matches/request", matchRequest, {
                headers: { Authorization: `Bearer ${token}` },
            });

            setMessage("Match request sent successfully!");
            fetchAvailableMatches(); // Refresh the list
        } catch (error) {
            setMessage("Failed to send match request");
        }
    };

    const formatCompatibilityScore = (score: number) => {
        return Math.round(score * 100);
    };

    if (loading) {
        return (
            <div className="match-finder">
                <div className="loading-spinner">
                    <div className="spinner"></div>
                    <p>Finding your perfect matches...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="match-finder">
            <div className="match-finder-header">
                <h1>☕ Find Your Coffee Chat Match</h1>
                <p>
                    Discover professionals who share your interests and schedule
                    a coffee chat
                </p>
            </div>

            {message && (
                <div
                    className={`message ${
                        message.includes("successfully")
                            ? "success-message"
                            : "error-message"
                    }`}
                >
                    {message}
                </div>
            )}

            <div className="matches-grid">
                {availableMatches.length === 0 ? (
                    <div className="no-matches">
                        <h3>No matches available right now</h3>
                        <p>
                            Check back later or update your preferences to find
                            more matches!
                        </p>
                    </div>
                ) : (
                    availableMatches.map((match, index) => (
                        <div key={index} className="match-card">
                            <div className="match-header">
                                <div className="compatibility-badge">
                                    {formatCompatibilityScore(
                                        match.compatibilityScore
                                    )}
                                    % Match
                                </div>
                                <div className="match-type">
                                    {match.matchType.replace("_", " ")}
                                </div>
                            </div>

                            <div className="match-content">
                                <h3>{match.user2Name}</h3>
                                <p className="user-email">{match.user2Email}</p>

                                <div className="compatibility-bar">
                                    <div
                                        className="compatibility-fill"
                                        style={{
                                            width: `${formatCompatibilityScore(
                                                match.compatibilityScore
                                            )}%`,
                                        }}
                                    ></div>
                                </div>

                                <div className="match-actions">
                                    <button
                                        className="btn btn-primary"
                                        onClick={() =>
                                            sendMatchRequest(match.user2Id)
                                        }
                                    >
                                        Request Coffee Chat
                                    </button>
                                    <button className="btn btn-secondary">
                                        View Profile
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            <div className="match-finder-footer">
                <button
                    className="btn btn-secondary"
                    onClick={fetchAvailableMatches}
                >
                    Refresh Matches
                </button>
            </div>
        </div>
    );
};

export default MatchFinder;
