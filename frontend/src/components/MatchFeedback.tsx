import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './MatchFeedback.css';

interface MatchFeedback {
  id: number;
  matchId: number;
  participantName: string;
  overallRating: number;
  conversationQuality: number;
  personalityMatch: number;
  professionalRelevance: number;
  wouldMeetAgain: boolean;
  comments: string;
  submittedAt: string;
}

interface FeedbackStats {
  averageRating: number;
  totalFeedbacks: number;
  positivePercentage: number;
  topStrengths: string[];
  improvementAreas: string[];
}

interface FeedbackForm {
  matchId: number;
  overallRating: number;
  conversationQuality: number;
  personalityMatch: number;
  professionalRelevance: number;
  wouldMeetAgain: boolean;
  comments: string;
}

const MatchFeedback: React.FC = () => {
  const [feedbacks, setFeedbacks] = useState<MatchFeedback[]>([]);
  const [stats, setStats] = useState<FeedbackStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [showFeedbackForm, setShowFeedbackForm] = useState(false);
  const [feedbackForm, setFeedbackForm] = useState<FeedbackForm>({
    matchId: 0,
    overallRating: 5,
    conversationQuality: 5,
    personalityMatch: 5,
    professionalRelevance: 5,
    wouldMeetAgain: true,
    comments: ''
  });

  useEffect(() => {
    fetchFeedbacks();
    fetchStats();
  }, []);

  const fetchFeedbacks = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('/api/match-feedback/my-feedback', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setFeedbacks(response.data);
    } catch (error) {
      console.error('Error fetching feedbacks:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchStats = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('/api/match-feedback/stats', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching stats:', error);
    }
  };

  const handleSubmitFeedback = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      await axios.post('/api/match-feedback/submit', feedbackForm, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      setShowFeedbackForm(false);
      setFeedbackForm({
        matchId: 0,
        overallRating: 5,
        conversationQuality: 5,
        personalityMatch: 5,
        professionalRelevance: 5,
        wouldMeetAgain: true,
        comments: ''
      });
      fetchFeedbacks();
      fetchStats();
    } catch (error) {
      console.error('Error submitting feedback:', error);
    }
  };

  const renderStars = (rating: number, interactive: boolean = false, onChange?: (rating: number) => void) => {
    return (
      <div className="star-rating">
        {[1, 2, 3, 4, 5].map((star) => (
          <span
            key={star}
            className={`star ${star <= rating ? 'filled' : ''} ${interactive ? 'interactive' : ''}`}
            onClick={interactive && onChange ? () => onChange(star) : undefined}
          >
            ★
          </span>
        ))}
      </div>
    );
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  if (loading) {
    return (
      <div className="match-feedback-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading feedback data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="match-feedback-container">
      <div className="feedback-header">
        <h1>📊 Match Feedback</h1>
        <p>Help us improve your matching experience</p>
        <button 
          className="btn btn-primary"
          onClick={() => setShowFeedbackForm(true)}
        >
          Submit New Feedback
        </button>
      </div>

      {/* Statistics Section */}
      {stats && (
        <div className="stats-section">
          <h2>Your Feedback Statistics</h2>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-value">{stats.averageRating.toFixed(1)}</div>
              <div className="stat-label">Average Rating</div>
              {renderStars(Math.round(stats.averageRating))}
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.totalFeedbacks}</div>
              <div className="stat-label">Total Feedbacks</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{stats.positivePercentage}%</div>
              <div className="stat-label">Positive Matches</div>
            </div>
          </div>
        </div>
      )}

      {/* Feedback History */}
      <div className="feedback-history">
        <h2>Your Feedback History</h2>
        {feedbacks.length === 0 ? (
          <div className="no-feedback">
            <p>No feedback submitted yet. Start by providing feedback on your recent matches!</p>
          </div>
        ) : (
          <div className="feedback-list">
            {feedbacks.map((feedback) => (
              <div key={feedback.id} className="feedback-card">
                <div className="feedback-header-card">
                  <div className="feedback-info">
                    <h3>{feedback.participantName}</h3>
                    <p className="feedback-date">{formatDate(feedback.submittedAt)}</p>
                  </div>
                  <div className="overall-rating">
                    {renderStars(feedback.overallRating)}
                    <span className="rating-text">{feedback.overallRating}/5</span>
                  </div>
                </div>

                <div className="feedback-details">
                  <div className="rating-breakdown">
                    <div className="rating-item">
                      <span>Conversation Quality:</span>
                      {renderStars(feedback.conversationQuality)}
                    </div>
                    <div className="rating-item">
                      <span>Personality Match:</span>
                      {renderStars(feedback.personalityMatch)}
                    </div>
                    <div className="rating-item">
                      <span>Professional Relevance:</span>
                      {renderStars(feedback.professionalRelevance)}
                    </div>
                  </div>

                  <div className="would-meet-again">
                    <span className={`meet-again-badge ${feedback.wouldMeetAgain ? 'positive' : 'negative'}`}>
                      {feedback.wouldMeetAgain ? '✅ Would meet again' : '❌ Would not meet again'}
                    </span>
                  </div>

                  {feedback.comments && (
                    <div className="feedback-comments">
                      <h4>Comments:</h4>
                      <p>{feedback.comments}</p>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Feedback Form Modal */}
      {showFeedbackForm && (
        <div className="modal-overlay" onClick={() => setShowFeedbackForm(false)}>
          <div className="feedback-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Submit Match Feedback</h3>
              <button 
                className="close-btn"
                onClick={() => setShowFeedbackForm(false)}
              >
                ×
              </button>
            </div>
            
            <form onSubmit={handleSubmitFeedback} className="feedback-form">
              <div className="form-group">
                <label>Match ID</label>
                <input
                  type="number"
                  value={feedbackForm.matchId}
                  onChange={(e) => setFeedbackForm({
                    ...feedbackForm,
                    matchId: parseInt(e.target.value)
                  })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Overall Rating</label>
                {renderStars(feedbackForm.overallRating, true, (rating) => 
                  setFeedbackForm({ ...feedbackForm, overallRating: rating })
                )}
              </div>

              <div className="form-group">
                <label>Conversation Quality</label>
                {renderStars(feedbackForm.conversationQuality, true, (rating) => 
                  setFeedbackForm({ ...feedbackForm, conversationQuality: rating })
                )}
              </div>

              <div className="form-group">
                <label>Personality Match</label>
                {renderStars(feedbackForm.personalityMatch, true, (rating) => 
                  setFeedbackForm({ ...feedbackForm, personalityMatch: rating })
                )}
              </div>

              <div className="form-group">
                <label>Professional Relevance</label>
                {renderStars(feedbackForm.professionalRelevance, true, (rating) => 
                  setFeedbackForm({ ...feedbackForm, professionalRelevance: rating })
                )}
              </div>

              <div className="form-group">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={feedbackForm.wouldMeetAgain}
                    onChange={(e) => setFeedbackForm({
                      ...feedbackForm,
                      wouldMeetAgain: e.target.checked
                    })}
                  />
                  I would meet this person again
                </label>
              </div>

              <div className="form-group">
                <label>Additional Comments</label>
                <textarea
                  value={feedbackForm.comments}
                  onChange={(e) => setFeedbackForm({
                    ...feedbackForm,
                    comments: e.target.value
                  })}
                  placeholder="Share your thoughts about this match..."
                  rows={4}
                />
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowFeedbackForm(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Submit Feedback
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default MatchFeedback;
