import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import "./Auth.css";

const Register: React.FC = () => {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        industry: "",
        skills: "",
        interests: "",
        linkedinUrl: "",
        companyId: "",
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const { register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        if (formData.password.length < 6) {
            setError("Password must be at least 6 characters long");
            return;
        }

        setLoading(true);

        try {
            const userData = {
                name: formData.name,
                email: formData.email,
                password: formData.password,
                industry: formData.industry || null,
                skills: formData.skills || null,
                interests: formData.interests || null,
                linkedinUrl: formData.linkedinUrl || null,
                companyId: formData.companyId
                    ? parseInt(formData.companyId)
                    : null,
            };

            await register(userData);
            navigate("/login");
        } catch (err) {
            setError("Registration failed. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Join Water Cooler Network</h2>
                <p className="auth-subtitle">
                    Connect with professionals and build meaningful
                    relationships
                </p>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="name">Full Name *</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            placeholder="Enter your full name"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Email *</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            placeholder="Enter your email"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password *</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            placeholder="Enter your password"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="confirmPassword">
                            Confirm Password *
                        </label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                            placeholder="Confirm your password"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="industry">Industry</label>
                        <input
                            type="text"
                            id="industry"
                            name="industry"
                            value={formData.industry}
                            onChange={handleChange}
                            placeholder="e.g., Technology, Healthcare, Finance"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="skills">Skills</label>
                        <textarea
                            id="skills"
                            name="skills"
                            value={formData.skills}
                            onChange={handleChange}
                            placeholder="List your key skills (comma-separated)"
                            rows={3}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="interests">Interests</label>
                        <textarea
                            id="interests"
                            name="interests"
                            value={formData.interests}
                            onChange={handleChange}
                            placeholder="Share your professional interests"
                            rows={3}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="linkedinUrl">LinkedIn Profile</label>
                        <input
                            type="url"
                            id="linkedinUrl"
                            name="linkedinUrl"
                            value={formData.linkedinUrl}
                            onChange={handleChange}
                            placeholder="https://linkedin.com/in/yourprofile"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="companyId">Company ID (Optional)</label>
                        <input
                            type="number"
                            id="companyId"
                            name="companyId"
                            value={formData.companyId}
                            onChange={handleChange}
                            placeholder="Enter company ID if applicable"
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-button"
                        disabled={loading}
                    >
                        {loading ? "Creating Account..." : "Create Account"}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>
                        Already have an account?{" "}
                        <Link to="/login">Sign in here</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;
