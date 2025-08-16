import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './SmartMatching.css';

interface SmartMatch {
  userId: number;
  name: string;
  email: string;
  industry: string;
  skills: string;
  interests: string;
  linkedinUrl: string;
  compatibilityScore: number;
  matchReason: string;
}

interface ConversationStarter {
  id: number;
  template: string;
  category: string;
  personalizedContent: string;
}

const SmartMatching: React.FC = () => {
  const [matches, setMatches] = useState<SmartMatch[]>([]);
  const [conversationStarters, setConversationStarters] = useState<ConversationStarter[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedMatch, setSelectedMatch] = useState<SmartMatch | null>(null);
  const [showStarters, setShowStarters] = useState(false);

  useEffect(() => {
    fetchSmartMatches();
  }, []);

  const fetchSmartMatches = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('/api/smart-matching/recommendations?limit=10', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMatches(response.data);
    } catch (error) {
      console.error('Error fetching smart matches:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchConversationStarters = async (targetUserId: number) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`/api/conversation-starters/personalized/${targetUserId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setConversationStarters(response.data);
      setShowStarters(true);
    } catch (error) {
      console.error('Error fetching conversation starters:', error);
    }
  };

  const recordInteraction = async (targetUserId: number, interactionType: string, value?: string) => {
    try {
      const token = localStorage.getItem('token');
      await axios.post('/api/smart-matching/interaction', null, {
        params: { targetUserId, interactionType, value },
        headers: { Authorization: `Bearer ${token}` }
      });
    } catch (error) {
      console.error('Error recording interaction:', error);
    }
  };

  const handleViewProfile = (match: SmartMatch) => {
    setSelectedMatch(match);
    recordInteraction(match.userId, 'VIEW');
  };

  const handleStartConversation = (match: SmartMatch) => {
    fetchConversationStarters(match.userId);
    recordInteraction(match.userId, 'MESSAGE');
  };

  const handleLike = (match: SmartMatch) => {
    recordInteraction(match.userId, 'LIKE');
    // Update UI to show liked state
    setMatches(prev => prev.map(m => 
      m.userId === match.userId ? { ...m, liked: true } : m
    ));
  };

  const handlePass = (match: SmartMatch) => {
    recordInteraction(match.userId, 'PASS');
    // Remove from current matches
    setMatches(prev => prev.filter(m => m.userId !== match.userId));
  };

  const getCompatibilityColor = (score: number) => {
    if (score >= 0.8) return '#4CAF50';
    if (score >= 0.6) return '#FF9800';
    return '#F44336';
  };

  const getCompatibilityLabel = (score: number) => {
    if (score >= 0.8) return 'Excellent Match';
    if (score >= 0.6) return 'Good Match';
    return 'Fair Match';
  };

  if (loading) {
    return (
      <div className="smart-matching-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Finding your perfect matches...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="smart-matching-container">
      <div className="smart-matching-header">
        <h1>🤖 Smart Matching</h1>
        <p>AI-powered connections based on your interests, skills, and goals</p>
      </div>

      <div className="matches-grid">
        {matches.map((match) => (
          <div key={match.userId} className="match-card">
            <div className="match-header">
              <div className="match-avatar">
                {match.name.charAt(0).toUpperCase()}
              </div>
              <div className="match-info">
                <h3>{match.name}</h3>
                <p className="match-industry">{match.industry}</p>
              </div>
              <div 
                className="compatibility-score"
                style={{ backgroundColor: getCompatibilityColor(match.compatibilityScore) }}
              >
                {Math.round(match.compatibilityScore * 100)}%
              </div>
            </div>

            <div className="match-details">
              <div className="match-section">
                <h4>Skills</h4>
                <div className="tags">
                  {match.skills?.split(',').map((skill, index) => (
                    <span key={index} className="tag skill-tag">
                      {skill.trim()}
                    </span>
                  ))}
                </div>
              </div>

              <div className="match-section">
                <h4>Interests</h4>
                <div className="tags">
                  {match.interests?.split(',').map((interest, index) => (
                    <span key={index} className="tag interest-tag">
                      {interest.trim()}
                    </span>
                  ))}
                </div>
              </div>

              <div className="match-reason">
                <p>{match.matchReason}</p>
              </div>
            </div>

            <div className="match-actions">
              <button 
                className="btn btn-secondary"
                onClick={() => handleViewProfile(match)}
              >
                View Profile
              </button>
              <button 
                className="btn btn-primary"
                onClick={() => handleStartConversation(match)}
              >
                Start Chat
              </button>
              <div className="match-reactions">
                <button 
                  className="reaction-btn like-btn"
                  onClick={() => handleLike(match)}
                >
                  👍
                </button>
                <button 
                  className="reaction-btn pass-btn"
                  onClick={() => handlePass(match)}
                >
                  👎
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {matches.length === 0 && (
        <div className="no-matches">
          <h3>No matches found</h3>
          <p>Try updating your profile or interests to find better matches!</p>
        </div>
      )}

      {/* Conversation Starters Modal */}
      {showStarters && selectedMatch && (
        <div className="modal-overlay" onClick={() => setShowStarters(false)}>
          <div className="conversation-starters-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Conversation Starters with {selectedMatch.name}</h3>
              <button 
                className="close-btn"
                onClick={() => setShowStarters(false)}
              >
                ×
              </button>
            </div>
            <div className="starters-list">
              {conversationStarters.map((starter) => (
                <div key={starter.id} className="starter-card">
                  <div className="starter-category">{starter.category}</div>
                  <p className="starter-text">{starter.personalizedContent}</p>
                  <button className="use-starter-btn">
                    Use This Starter
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Profile Modal */}
      {selectedMatch && !showStarters && (
        <div className="modal-overlay" onClick={() => setSelectedMatch(null)}>
          <div className="profile-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{selectedMatch.name}'s Profile</h3>
              <button 
                className="close-btn"
                onClick={() => setSelectedMatch(null)}
              >
                ×
              </button>
            </div>
            <div className="profile-content">
              <div className="profile-section">
                <h4>Industry</h4>
                <p>{selectedMatch.industry}</p>
              </div>
              <div className="profile-section">
                <h4>Skills</h4>
                <p>{selectedMatch.skills}</p>
              </div>
              <div className="profile-section">
                <h4>Interests</h4>
                <p>{selectedMatch.interests}</p>
              </div>
              <div className="profile-section">
                <h4>Compatibility</h4>
                <div className="compatibility-details">
                  <div 
                    className="compatibility-bar"
                    style={{ backgroundColor: getCompatibilityColor(selectedMatch.compatibilityScore) }}
                  >
                    <span>{getCompatibilityLabel(selectedMatch.compatibilityScore)}</span>
                  </div>
                </div>
              </div>
              {selectedMatch.linkedinUrl && (
                <div className="profile-section">
                  <a 
                    href={selectedMatch.linkedinUrl} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="linkedin-link"
                  >
                    View LinkedIn Profile
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SmartMatching;
