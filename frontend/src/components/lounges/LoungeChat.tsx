import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../contexts/AuthContext";
import "./Lounges.css";

interface LoungeMessage {
    id: number;
    loungeId: number;
    userId: number;
    userName: string;
    userEmail: string;
    content: string;
    messageType: string;
    replyToMessageId: number | null;
    replyToUserName: string | null;
    isEdited: boolean;
    editedAt: string | null;
    isDeleted: boolean;
    createdAt: string;
}

interface Lounge {
    id: number;
    title: string;
    description: string;
    topic: string;
    category: string;
    creatorName: string;
    visibility: string;
    maxParticipants: number;
    currentParticipants: number;
    isParticipant: boolean;
    participantRole: string;
}

const LoungeChat: React.FC = () => {
    const { loungeId } = useParams<{ loungeId: string }>();
    const { token, user } = useAuth();
    const navigate = useNavigate();
    const [lounge, setLounge] = useState<Lounge | null>(null);
    const [messages, setMessages] = useState<LoungeMessage[]>([]);
    const [newMessage, setNewMessage] = useState("");
    const [loading, setLoading] = useState(false);
    const [messageLoading, setMessageLoading] = useState(false);
    const [message, setMessage] = useState("");
    const [replyTo, setReplyTo] = useState<LoungeMessage | null>(null);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (loungeId) {
            fetchLounge();
            fetchMessages();
        }
    }, [loungeId]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const fetchLounge = async () => {
        try {
            const response = await axios.get(`/api/lounges/${loungeId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setLounge(response.data);
        } catch (error) {
            setMessage("Failed to fetch lounge details");
        }
    };

    const fetchMessages = async () => {
        setMessageLoading(true);
        try {
            const response = await axios.get(
                `/api/lounges/${loungeId}/messages`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setMessages(response.data);
        } catch (error) {
            setMessage("Failed to fetch messages");
        } finally {
            setMessageLoading(false);
        }
    };

    const sendMessage = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!newMessage.trim()) return;

        try {
            const messageData = {
                loungeId: parseInt(loungeId!),
                content: newMessage,
                messageType: "TEXT",
                replyToMessageId: replyTo?.id || null,
            };

            const response = await axios.post(
                `/api/lounges/${loungeId}/messages`,
                messageData,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            setMessages((prev) => [...prev, response.data]);
            setNewMessage("");
            setReplyTo(null);
        } catch (error) {
            setMessage("Failed to send message");
        }
    };

    const joinLounge = async () => {
        try {
            await axios.post(
                `/api/lounges/${loungeId}/join`,
                {},
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setMessage("Successfully joined the lounge!");
            fetchLounge(); // Refresh lounge data
        } catch (error) {
            setMessage("Failed to join lounge");
        }
    };

    const leaveLounge = async () => {
        try {
            await axios.post(
                `/api/lounges/${loungeId}/leave`,
                {},
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            setMessage("Left the lounge");
            navigate("/lounges");
        } catch (error) {
            setMessage("Failed to leave lounge");
        }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getMessageTypeIcon = (messageType: string) => {
        switch (messageType) {
            case "SYSTEM":
                return "🔔";
            case "JOIN":
                return "👋";
            case "LEAVE":
                return "👋";
            case "TOPIC_CHANGE":
                return "🔄";
            case "MODERATOR_ACTION":
                return "🛡️";
            default:
                return "";
        }
    };

    const getMessageTypeClass = (messageType: string) => {
        switch (messageType) {
            case "SYSTEM":
                return "system-message";
            case "JOIN":
                return "join-message";
            case "LEAVE":
                return "leave-message";
            case "TOPIC_CHANGE":
                return "topic-change-message";
            case "MODERATOR_ACTION":
                return "moderator-message";
            default:
                return "";
        }
    };

    if (!lounge) {
        return (
            <div className="lounge-chat">
                <div className="loading-spinner">
                    <div className="spinner"></div>
                    <p>Loading lounge...</p>
                </div>
            </div>
        );
    }

    if (!lounge.isParticipant) {
        return (
            <div className="lounge-chat">
                <div className="join-prompt">
                    <h2>Join {lounge.title}</h2>
                    <p>{lounge.description}</p>
                    <div className="lounge-info">
                        <p>
                            <strong>Topic:</strong> {lounge.topic}
                        </p>
                        <p>
                            <strong>Category:</strong> {lounge.category}
                        </p>
                        <p>
                            <strong>Participants:</strong>{" "}
                            {lounge.currentParticipants}/
                            {lounge.maxParticipants}
                        </p>
                        <p>
                            <strong>Created by:</strong> {lounge.creatorName}
                        </p>
                    </div>
                    <button onClick={joinLounge} className="btn btn-primary">
                        Join Lounge
                    </button>
                    <button
                        onClick={() => navigate("/lounges")}
                        className="btn btn-secondary"
                    >
                        Back to Lounges
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="lounge-chat">
            <div className="chat-header">
                <div className="lounge-info-header">
                    <h1>{lounge.title}</h1>
                    <p className="lounge-topic">#{lounge.topic}</p>
                    <div className="lounge-meta">
                        <span className="participant-count">
                            👥 {lounge.currentParticipants}/
                            {lounge.maxParticipants}
                        </span>
                        <span className="visibility-badge">
                            {lounge.visibility === "PUBLIC"
                                ? "🌍 Public"
                                : lounge.visibility === "PRIVATE"
                                ? "🔒 Private"
                                : "🏢 Corporate"}
                        </span>
                        {lounge.participantRole !== "MEMBER" && (
                            <span className="role-badge">
                                {lounge.participantRole}
                            </span>
                        )}
                    </div>
                </div>

                <div className="chat-actions">
                    <button onClick={leaveLounge} className="btn btn-secondary">
                        Leave Lounge
                    </button>
                    {lounge.participantRole === "CREATOR" && (
                        <button className="btn btn-secondary">
                            Manage Lounge
                        </button>
                    )}
                </div>
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

            <div className="chat-container">
                <div className="messages-container">
                    {messageLoading ? (
                        <div className="loading-messages">
                            <div className="spinner"></div>
                            <p>Loading messages...</p>
                        </div>
                    ) : (
                        <>
                            {messages.length === 0 ? (
                                <div className="no-messages">
                                    <p>
                                        No messages yet. Start the conversation!
                                    </p>
                                </div>
                            ) : (
                                messages.map((msg) => (
                                    <div
                                        key={msg.id}
                                        className={`message-item ${getMessageTypeClass(
                                            msg.messageType
                                        )}`}
                                    >
                                        {msg.messageType !== "TEXT" ? (
                                            <div className="system-message-content">
                                                <span className="message-icon">
                                                    {getMessageTypeIcon(
                                                        msg.messageType
                                                    )}
                                                </span>
                                                <span className="system-text">
                                                    {msg.content}
                                                </span>
                                                <span className="message-time">
                                                    {formatDate(msg.createdAt)}
                                                </span>
                                            </div>
                                        ) : (
                                            <>
                                                <div className="message-header">
                                                    <span className="user-name">
                                                        {msg.userName}
                                                    </span>
                                                    <span className="message-time">
                                                        {formatDate(
                                                            msg.createdAt
                                                        )}
                                                    </span>
                                                </div>

                                                {msg.replyToUserName && (
                                                    <div className="reply-to">
                                                        Replying to{" "}
                                                        <strong>
                                                            {
                                                                msg.replyToUserName
                                                            }
                                                        </strong>
                                                    </div>
                                                )}

                                                <div className="message-content">
                                                    {msg.content}
                                                </div>

                                                <div className="message-actions">
                                                    <button
                                                        onClick={() =>
                                                            setReplyTo(msg)
                                                        }
                                                        className="reply-button"
                                                    >
                                                        Reply
                                                    </button>
                                                </div>
                                            </>
                                        )}
                                    </div>
                                ))
                            )}
                            <div ref={messagesEndRef} />
                        </>
                    )}
                </div>

                <div className="message-input-container">
                    {replyTo && (
                        <div className="reply-preview">
                            <span>Replying to {replyTo.userName}:</span>
                            <span className="reply-content">
                                {replyTo.content}
                            </span>
                            <button
                                onClick={() => setReplyTo(null)}
                                className="cancel-reply"
                            >
                                ×
                            </button>
                        </div>
                    )}

                    <form onSubmit={sendMessage} className="message-form">
                        <input
                            type="text"
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            placeholder="Type your message..."
                            className="message-input"
                            maxLength={1000}
                        />
                        <button
                            type="submit"
                            className="send-button"
                            disabled={!newMessage.trim()}
                        >
                            Send
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default LoungeChat;
