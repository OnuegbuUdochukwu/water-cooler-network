import React, { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";
import axios from "axios";
import "./Profile.css";

interface ProfileData {
    name: string;
    industry: string;
    skills: string;
    interests: string;
    linkedinUrl: string;
}

const Profile: React.FC = () => {
    const { user, token } = useAuth();
    const [profileData, setProfileData] = useState<ProfileData>({
        name: "",
        industry: "",
        skills: "",
        interests: "",
        linkedinUrl: "",
    });
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        if (user) {
            setProfileData({
                name: user.name || "",
                industry: user.industry || "",
                skills: user.skills || "",
                interests: user.interests || "",
                linkedinUrl: user.linkedinUrl || "",
            });
        }
    }, [user]);

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        setProfileData({
            ...profileData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");

        try {
            const response = await axios.put(
                `/api/users/profile/${user?.id}`,
                profileData,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            setMessage("Profile updated successfully!");
            setIsEditing(false);
        } catch (error) {
            setMessage("Failed to update profile. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        if (user) {
            setProfileData({
                name: user.name || "",
                industry: user.industry || "",
                skills: user.skills || "",
                interests: user.interests || "",
                linkedinUrl: user.linkedinUrl || "",
            });
        }
        setIsEditing(false);
        setMessage("");
    };

    return (
        <div className="profile">
            <div className="profile-header">
                <h1>Profile</h1>
                <p>Manage your professional information</p>
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

            <div className="profile-content">
                <div className="profile-card card">
                    <div className="card-header">
                        <h2 className="card-title">Personal Information</h2>
                        {!isEditing && (
                            <button
                                className="btn btn-primary"
                                onClick={() => setIsEditing(true)}
                            >
                                Edit Profile
                            </button>
                        )}
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="profile-form">
                            <div className="form-group">
                                <label htmlFor="name">Full Name</label>
                                <input
                                    type="text"
                                    id="name"
                                    name="name"
                                    value={profileData.name}
                                    onChange={handleChange}
                                    disabled={!isEditing}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="email">Email</label>
                                <input
                                    type="email"
                                    id="email"
                                    value={user?.email || ""}
                                    disabled
                                    className="disabled-input"
                                />
                                <small>Email cannot be changed</small>
                            </div>

                            <div className="form-group">
                                <label htmlFor="industry">Industry</label>
                                <input
                                    type="text"
                                    id="industry"
                                    name="industry"
                                    value={profileData.industry}
                                    onChange={handleChange}
                                    disabled={!isEditing}
                                    placeholder="e.g., Technology, Healthcare, Finance"
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="skills">Skills</label>
                                <textarea
                                    id="skills"
                                    name="skills"
                                    value={profileData.skills}
                                    onChange={handleChange}
                                    disabled={!isEditing}
                                    placeholder="List your key skills (comma-separated)"
                                    rows={3}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="interests">Interests</label>
                                <textarea
                                    id="interests"
                                    name="interests"
                                    value={profileData.interests}
                                    onChange={handleChange}
                                    disabled={!isEditing}
                                    placeholder="Share your professional interests"
                                    rows={3}
                                />
                            </div>

                            <div className="form-group">
                                <label htmlFor="linkedinUrl">
                                    LinkedIn Profile
                                </label>
                                <input
                                    type="url"
                                    id="linkedinUrl"
                                    name="linkedinUrl"
                                    value={profileData.linkedinUrl}
                                    onChange={handleChange}
                                    disabled={!isEditing}
                                    placeholder="https://linkedin.com/in/yourprofile"
                                />
                            </div>

                            {isEditing && (
                                <div className="form-actions">
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        disabled={loading}
                                    >
                                        {loading ? "Saving..." : "Save Changes"}
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-secondary"
                                        onClick={handleCancel}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            )}
                        </div>
                    </form>
                </div>

                <div className="profile-stats card">
                    <div className="card-header">
                        <h2 className="card-title">Account Information</h2>
                    </div>
                    <div className="stats-grid">
                        <div className="stat-item">
                            <span className="stat-label">Role</span>
                            <span className="stat-value">{user?.role}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Company ID</span>
                            <span className="stat-value">
                                {user?.companyId || "Not assigned"}
                            </span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Member Since</span>
                            <span className="stat-value">Today</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
