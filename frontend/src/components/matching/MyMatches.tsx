import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../../contexts/AuthContext";
import "./Matching.css";

interface Match {
    id: number;
    user1Id: number;
    user2Id: number;
    user1Name: string;
    user2Name: string;
    user1Email: string;
    user2Email: string;
    matchType: string;
    status: string;
    scheduledTime: string;
    durationMinutes: number;
    compatibilityScore: number;
    matchReason: string;
    createdAt: string;
}

const MyMatches: React.FC = () => {
    const { token, user } = useAuth();
    const [matches, setMatches] = useState<Match[]>([]);
    const [loading, setLoading] = useState(false);
    const [selectedStatus, setSelectedStatus] = useState<string>("all");

    useEffect(() => {
        fetchMatches();
    }, [selectedStatus]);

    const fetchMatches = async () => {
        setLoading(true);
        try {
            const endpoint =
                selectedStatus === "all"
                    ? "/api/matches/my-matches"
                    : `/api/matches/my-matches/${selectedStatus}`;
            const response = await axios.get(endpoint, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setMatches(response.data);
        } catch (error) {
            console.error("Failed to fetch matches:", error);
        } finally {
            setLoading(false);
        }
    };

    const respondToMatch = async (
        matchId: number,
        status: "ACCEPTED" | "REJECTED"
    ) => {
        try {
            const response = {
                status,
                message:
                    status === "ACCEPTED"
                        ? "Looking forward to our chat!"
                        : "Thanks for the offer, but I'll pass for now.",
                scheduledTime:
                    status === "ACCEPTED"
                        ? new Date(
                              Date.now() + 24 * 60 * 60 * 1000
                          ).toISOString()
                        : null,
                durationMinutes: 30,
            };

            await axios.post(`/api/matches/${matchId}/respond`, response, {
                headers: { Authorization: `Bearer ${token}` },
            });

            fetchMatches(); // Refresh the list
        } catch (error) {
            console.error("Failed to respond to match:", error);
        }
    };

    const getOtherUserName = (match: Match) => {
        return user?.id === match.user1Id ? match.user2Name : match.user1Name;
    };

    const getOtherUserEmail = (match: Match) => {
        return user?.id === match.user1Id ? match.user2Email : match.user1Email;
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString("en-US", {
            year: "numeric",
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getStatusBadgeClass = (status: string) => {
        switch (status) {
            case "PENDING":
                return "status-pending";
            case "ACCEPTED":
                return "status-accepted";
            case "REJECTED":
                return "status-rejected";
            case "SCHEDULED":
                return "status-scheduled";
            case "IN_PROGRESS":
                return "status-in-progress";
            case "COMPLETED":
                return "status-completed";
            case "CANCELLED":
                return "status-cancelled";
            default:
                return "status-default";
        }
    };

    if (loading) {
        return (
            <div className="my-matches">
                <div className="loading-spinner">
                    <div className="spinner"></div>
                    <p>Loading your matches...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="my-matches">
            <div className="my-matches-header">
                <h1>💬 My Matches</h1>
                <p>Manage your coffee chat connections and conversations</p>
            </div>

            <div className="matches-filter">
                <select
                    value={selectedStatus}
                    onChange={(e) => setSelectedStatus(e.target.value)}
                    className="status-filter"
                >
                    <option value="all">All Matches</option>
                    <option value="pending">Pending</option>
                    <option value="accepted">Accepted</option>
                    <option value="scheduled">Scheduled</option>
                    <option value="in_progress">In Progress</option>
                    <option value="completed">Completed</option>
                    <option value="rejected">Rejected</option>
                    <option value="cancelled">Cancelled</option>
                </select>
            </div>

            <div className="matches-list">
                {matches.length === 0 ? (
                    <div className="no-matches">
                        <h3>No matches found</h3>
                        <p>
                            Start exploring to find your first coffee chat
                            match!
                        </p>
                    </div>
                ) : (
                    matches.map((match) => (
                        <div key={match.id} className="match-item">
                            <div className="match-info">
                                <div className="match-header">
                                    <h3>
                                        ☕ Coffee Chat with{" "}
                                        {getOtherUserName(match)}
                                    </h3>
                                    <span
                                        className={`status-badge ${getStatusBadgeClass(
                                            match.status
                                        )}`}
                                    >
                                        {match.status.replace("_", " ")}
                                    </span>
                                </div>

                                <div className="match-details">
                                    <p>
                                        <strong>Type:</strong>{" "}
                                        {match.matchType.replace("_", " ")}
                                    </p>
                                    <p>
                                        <strong>Compatibility:</strong>{" "}
                                        {Math.round(
                                            match.compatibilityScore * 100
                                        )}
                                        %
                                    </p>
                                    {match.scheduledTime && (
                                        <p>
                                            <strong>Scheduled:</strong>{" "}
                                            {formatDate(match.scheduledTime)}
                                        </p>
                                    )}
                                    <p>
                                        <strong>Duration:</strong>{" "}
                                        {match.durationMinutes} minutes
                                    </p>
                                    {match.matchReason && (
                                        <p>
                                            <strong>Reason:</strong>{" "}
                                            {match.matchReason}
                                        </p>
                                    )}
                                    <p>
                                        <strong>Created:</strong>{" "}
                                        {formatDate(match.createdAt)}
                                    </p>
                                </div>
                            </div>

                            <div className="match-actions">
                                {match.status === "PENDING" &&
                                    user?.id === match.user2Id && (
                                        <div className="pending-actions">
                                            <button
                                                className="btn btn-primary"
                                                onClick={() =>
                                                    respondToMatch(
                                                        match.id,
                                                        "ACCEPTED"
                                                    )
                                                }
                                            >
                                                Accept
                                            </button>
                                            <button
                                                className="btn btn-secondary"
                                                onClick={() =>
                                                    respondToMatch(
                                                        match.id,
                                                        "REJECTED"
                                                    )
                                                }
                                            >
                                                Decline
                                            </button>
                                        </div>
                                    )}

                                {match.status === "ACCEPTED" && (
                                    <button className="btn btn-primary">
                                        Schedule Chat
                                    </button>
                                )}

                                {match.status === "SCHEDULED" && (
                                    <button className="btn btn-primary">
                                        Join Chat
                                    </button>
                                )}

                                {match.status === "COMPLETED" && (
                                    <button className="btn btn-secondary">
                                        View Chat History
                                    </button>
                                )}
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default MyMatches;
