import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './MentorshipDashboard.css';

interface MentorshipProgram {
    id: number;
    programName: string;
    description: string;
    programType: string;
    programTypeDisplay: string;
    durationWeeks: number;
    maxMenteesPerMentor: number;
    minMentorExperienceYears: number;
    isActive: boolean;
    startDate: string;
    endDate: string;
    currentParticipants: number;
    maxParticipants: number;
    statusDisplay: string;
    getIsFull?: () => boolean;
}

interface MentorshipRelationship {
    id: number;
    program: {
        id: number;
        programName: string;
    };
    mentorId: number;
    menteeId: number;
    status: string;
    startDate: string;
    endDate: string;
    goals: string;
    notes: string;
    mentorRating: number;
    menteeRating: number;
    mentorFeedback: string;
    menteeFeedback: string;
}

interface MentorshipSession {
    id: number;
    sessionDate: string;
    durationMinutes: number;
    sessionType: string;
    title: string;
    description: string;
    agenda: string;
    notes: string;
    actionItems: string;
    status: string;
}

const MentorshipDashboard: React.FC = () => {
    const [activeTab, setActiveTab] = useState<'programs' | 'relationships' | 'sessions'>('programs');
    const [programs, setPrograms] = useState<MentorshipProgram[]>([]);
    const [relationships, setRelationships] = useState<MentorshipRelationship[]>([]);
    const [sessions, setSessions] = useState<MentorshipSession[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedCompany, setSelectedCompany] = useState<number>(1); // Default company ID
    const [currentUserId, setCurrentUserId] = useState<number>(1); // Default user ID

    useEffect(() => {
        fetchData();
    }, [selectedCompany, activeTab]);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            switch (activeTab) {
                case 'programs':
                    const programsResponse = await axios.get(`/api/mentorship/programs/company/${selectedCompany}`);
                    setPrograms(programsResponse.data);
                    break;
                case 'relationships':
                    const relationshipsResponse = await axios.get(`/api/mentorship/relationships/user/${currentUserId}`);
                    setRelationships(relationshipsResponse.data);
                    break;
                case 'sessions':
                    // Fetch sessions for all user relationships
                    const relationshipsForSessions = await axios.get(`/api/mentorship/relationships/user/${currentUserId}`);
                    const allSessions: MentorshipSession[] = [];
                    for (const rel of relationshipsForSessions.data) {
                        const sessionsResponse = await axios.get(`/api/mentorship/sessions/relationship/${rel.id}`);
                        allSessions.push(...sessionsResponse.data);
                    }
                    setSessions(allSessions);
                    break;
            }
        } catch (err) {
            setError('Failed to fetch data');
            console.error('Error fetching data:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleTabChange = (tab: 'programs' | 'relationships' | 'sessions') => {
        setActiveTab(tab);
    };

    const handleCompanyChange = (companyId: number) => {
        setSelectedCompany(companyId);
    };

    const handleJoinProgram = async (programId: number) => {
        try {
            // This would typically open a modal to select mentor/mentee role and goals
            alert('Join program functionality coming soon!');
        } catch (err) {
            console.error('Error joining program:', err);
        }
    };

    const handleCreateSession = async (relationshipId: number) => {
        try {
            // This would typically open a modal to create a new session
            alert('Create session functionality coming soon!');
        } catch (err) {
            console.error('Error creating session:', err);
        }
    };

    if (loading) {
        return (
            <div className="mentorship-dashboard">
                <div className="loading-spinner">Loading mentorship data...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="mentorship-dashboard">
                <div className="error-message">{error}</div>
                <button onClick={fetchData} className="retry-button">
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="mentorship-dashboard">
            <div className="dashboard-header">
                <h1>Mentorship Dashboard</h1>
                <div className="dashboard-controls">
                    <div className="company-selector">
                        <label htmlFor="company-select">Company:</label>
                        <select
                            id="company-select"
                            value={selectedCompany}
                            onChange={(e) => handleCompanyChange(Number(e.target.value))}
                        >
                            <option value={1}>Company 1</option>
                            <option value={2}>Company 2</option>
                            <option value={3}>Company 3</option>
                        </select>
                    </div>
                </div>
            </div>

            <div className="tab-navigation">
                <button
                    className={`tab-button ${activeTab === 'programs' ? 'active' : ''}`}
                    onClick={() => handleTabChange('programs')}
                >
                    📚 Programs
                </button>
                <button
                    className={`tab-button ${activeTab === 'relationships' ? 'active' : ''}`}
                    onClick={() => handleTabChange('relationships')}
                >
                    🤝 Relationships
                </button>
                <button
                    className={`tab-button ${activeTab === 'sessions' ? 'active' : ''}`}
                    onClick={() => handleTabChange('sessions')}
                >
                    📅 Sessions
                </button>
            </div>

            <div className="tab-content">
                {activeTab === 'programs' && (
                    <div className="programs-section">
                        <h2>Available Mentorship Programs</h2>
                        <div className="programs-grid">
                            {programs.map((program) => (
                                <div key={program.id} className="program-card">
                                    <div className="program-header">
                                        <h3>{program.programName}</h3>
                                        <span className={`status-badge ${program.statusDisplay.toLowerCase()}`}>
                                            {program.statusDisplay}
                                        </span>
                                    </div>
                                    <p className="program-description">{program.description}</p>
                                    <div className="program-details">
                                        <div className="detail-item">
                                            <span className="detail-label">Type:</span>
                                            <span className="detail-value">{program.programTypeDisplay}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Duration:</span>
                                            <span className="detail-value">{program.durationWeeks} weeks</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Participants:</span>
                                            <span className="detail-value">
                                                {program.currentParticipants}/{program.maxParticipants}
                                            </span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Min Experience:</span>
                                            <span className="detail-value">{program.minMentorExperienceYears} years</span>
                                        </div>
                                    </div>
                                    <div className="program-actions">
                                        <button
                                            className="join-button"
                                            onClick={() => handleJoinProgram(program.id)}
                                            disabled={program.getIsFull?.() || !program.isActive}
                                        >
                                            {program.getIsFull?.() ? 'Full' : 'Join Program'}
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'relationships' && (
                    <div className="relationships-section">
                        <h2>My Mentorship Relationships</h2>
                        <div className="relationships-grid">
                            {relationships.map((relationship) => (
                                <div key={relationship.id} className="relationship-card">
                                    <div className="relationship-header">
                                        <h3>{relationship.program.programName}</h3>
                                        <span className={`status-badge ${relationship.status.toLowerCase()}`}>
                                            {relationship.status}
                                        </span>
                                    </div>
                                    <div className="relationship-details">
                                        <div className="detail-item">
                                            <span className="detail-label">Role:</span>
                                            <span className="detail-value">
                                                {relationship.mentorId === currentUserId ? 'Mentor' : 'Mentee'}
                                            </span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Goals:</span>
                                            <span className="detail-value">{relationship.goals}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Started:</span>
                                            <span className="detail-value">
                                                {new Date(relationship.startDate).toLocaleDateString()}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="relationship-actions">
                                        <button
                                            className="create-session-button"
                                            onClick={() => handleCreateSession(relationship.id)}
                                        >
                                            Schedule Session
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {activeTab === 'sessions' && (
                    <div className="sessions-section">
                        <h2>Mentorship Sessions</h2>
                        <div className="sessions-grid">
                            {sessions.map((session) => (
                                <div key={session.id} className="session-card">
                                    <div className="session-header">
                                        <h3>{session.title}</h3>
                                        <span className={`status-badge ${session.status.toLowerCase()}`}>
                                            {session.status}
                                        </span>
                                    </div>
                                    <div className="session-details">
                                        <div className="detail-item">
                                            <span className="detail-label">Date:</span>
                                            <span className="detail-value">
                                                {new Date(session.sessionDate).toLocaleString()}
                                            </span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Duration:</span>
                                            <span className="detail-value">{session.durationMinutes} minutes</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Type:</span>
                                            <span className="detail-value">{session.sessionType}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Description:</span>
                                            <span className="detail-value">{session.description}</span>
                                        </div>
                                    </div>
                                    {session.agenda && (
                                        <div className="session-agenda">
                                            <h4>Agenda:</h4>
                                            <p>{session.agenda}</p>
                                        </div>
                                    )}
                                    {session.notes && (
                                        <div className="session-notes">
                                            <h4>Notes:</h4>
                                            <p>{session.notes}</p>
                                        </div>
                                    )}
                                    {session.actionItems && (
                                        <div className="session-actions">
                                            <h4>Action Items:</h4>
                                            <p>{session.actionItems}</p>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            <div className="actions-section">
                <button className="create-program-button" onClick={() => alert('Create program functionality coming soon!')}>
                    Create New Program
                </button>
                <button className="refresh-button" onClick={fetchData}>
                    Refresh Data
                </button>
            </div>
        </div>
    );
};

export default MentorshipDashboard;
