import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../contexts/AuthContext";
import "./Lounges.css";

interface CreateLoungeForm {
    title: string;
    description: string;
    topic: string;
    category: string;
    tags: string[];
    visibility: string;
    maxParticipants: number;
}

const CreateLounge: React.FC = () => {
    const { token } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState<CreateLoungeForm>({
        title: "",
        description: "",
        topic: "",
        category: "",
        tags: [],
        visibility: "PUBLIC",
        maxParticipants: 50,
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");
    const [tagInput, setTagInput] = useState("");

    const handleChange = (
        e: React.ChangeEvent<
            HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
        >
    ) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: name === "maxParticipants" ? parseInt(value) || 50 : value,
        }));
    };

    const addTag = () => {
        if (tagInput.trim() && !formData.tags.includes(tagInput.trim())) {
            setFormData((prev) => ({
                ...prev,
                tags: [...prev.tags, tagInput.trim()],
            }));
            setTagInput("");
        }
    };

    const removeTag = (tagToRemove: string) => {
        setFormData((prev) => ({
            ...prev,
            tags: prev.tags.filter((tag) => tag !== tagToRemove),
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");

        try {
            const response = await axios.post("/api/lounges", formData, {
                headers: { Authorization: `Bearer ${token}` },
            });

            setMessage("Lounge created successfully! Redirecting...");
            setTimeout(() => {
                navigate(`/lounges/${response.data.id}`);
            }, 1500);
        } catch (error: any) {
            setMessage(
                error.response?.data?.message || "Failed to create lounge"
            );
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === "Enter") {
            e.preventDefault();
            addTag();
        }
    };

    return (
        <div className="create-lounge">
            <div className="create-lounge-header">
                <h1>🏗️ Create New Topic Lounge</h1>
                <p>Start a conversation around your professional interests</p>
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

            <form onSubmit={handleSubmit} className="create-lounge-form">
                <div className="form-section">
                    <h3>Basic Information</h3>

                    <div className="form-group">
                        <label htmlFor="title">Lounge Title *</label>
                        <input
                            type="text"
                            id="title"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            required
                            placeholder="e.g., AI in Healthcare Discussion"
                            maxLength={100}
                        />
                        <small>
                            Choose a clear, descriptive title (3-100 characters)
                        </small>
                    </div>

                    <div className="form-group">
                        <label htmlFor="description">Description</label>
                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Describe what this lounge is about, what topics will be discussed..."
                            rows={4}
                            maxLength={500}
                        />
                        <small>
                            Optional description of your lounge (max 500
                            characters)
                        </small>
                    </div>
                </div>

                <div className="form-section">
                    <h3>Topic & Category</h3>

                    <div className="form-group">
                        <label htmlFor="topic">Primary Topic *</label>
                        <input
                            type="text"
                            id="topic"
                            name="topic"
                            value={formData.topic}
                            onChange={handleChange}
                            required
                            placeholder="e.g., AI, Remote Work, Leadership"
                            maxLength={100}
                        />
                        <small>
                            Main topic that will be discussed in this lounge
                        </small>
                    </div>

                    <div className="form-group">
                        <label htmlFor="category">Category</label>
                        <select
                            id="category"
                            name="category"
                            value={formData.category}
                            onChange={handleChange}
                        >
                            <option value="">
                                Select a category (optional)
                            </option>
                            <option value="Technology">Technology</option>
                            <option value="Healthcare">Healthcare</option>
                            <option value="Finance">Finance</option>
                            <option value="Education">Education</option>
                            <option value="Marketing">Marketing</option>
                            <option value="Design">Design</option>
                            <option value="Sales">Sales</option>
                            <option value="Operations">Operations</option>
                            <option value="Human Resources">
                                Human Resources
                            </option>
                            <option value="Legal">Legal</option>
                            <option value="Consulting">Consulting</option>
                        </select>
                        <small>
                            Industry or field category for better discovery
                        </small>
                    </div>
                </div>

                <div className="form-section">
                    <h3>Tags & Organization</h3>

                    <div className="form-group">
                        <label htmlFor="tags">Tags</label>
                        <div className="tag-input-container">
                            <input
                                type="text"
                                id="tagInput"
                                value={tagInput}
                                onChange={(e) => setTagInput(e.target.value)}
                                onKeyPress={handleKeyPress}
                                placeholder="Add tags to help others find your lounge..."
                                maxLength={50}
                            />
                            <button
                                type="button"
                                onClick={addTag}
                                className="btn btn-secondary"
                            >
                                Add Tag
                            </button>
                        </div>
                        <small>
                            Press Enter or click Add Tag to add multiple tags
                        </small>
                    </div>

                    {formData.tags.length > 0 && (
                        <div className="tags-display">
                            {formData.tags.map((tag, index) => (
                                <span key={index} className="tag">
                                    {tag}
                                    <button
                                        type="button"
                                        onClick={() => removeTag(tag)}
                                        className="remove-tag"
                                    >
                                        ×
                                    </button>
                                </span>
                            ))}
                        </div>
                    )}
                </div>

                <div className="form-section">
                    <h3>Settings & Privacy</h3>

                    <div className="form-group">
                        <label htmlFor="visibility">Visibility</label>
                        <select
                            id="visibility"
                            name="visibility"
                            value={formData.visibility}
                            onChange={handleChange}
                        >
                            <option value="PUBLIC">
                                🌍 Public - Anyone can find and join
                            </option>
                            <option value="PRIVATE">
                                🔒 Private - Invite-only access
                            </option>
                            <option value="CORPORATE">
                                🏢 Corporate - Company members only
                            </option>
                        </select>
                        <small>Choose who can see and join your lounge</small>
                    </div>

                    <div className="form-group">
                        <label htmlFor="maxParticipants">
                            Maximum Participants
                        </label>
                        <input
                            type="number"
                            id="maxParticipants"
                            name="maxParticipants"
                            value={formData.maxParticipants}
                            onChange={handleChange}
                            min="2"
                            max="200"
                        />
                        <small>
                            Set a limit on how many people can join (2-200)
                        </small>
                    </div>
                </div>

                <div className="form-actions">
                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={loading || !formData.title || !formData.topic}
                    >
                        {loading ? "Creating Lounge..." : "Create Lounge"}
                    </button>

                    <button
                        type="button"
                        onClick={() => navigate("/lounges")}
                        className="btn btn-secondary"
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateLounge;
