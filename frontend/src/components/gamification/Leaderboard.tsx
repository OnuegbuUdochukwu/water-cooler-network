import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Leaderboard.css';

interface LeaderboardEntry {
  userId: number;
  userName: string;
  userEmail: string;
  totalPoints: number;
  totalBadges: number;
  longestStreak: number;
  longestStreakType: string;
  rank: number;
  rankClass?: string;
  rankEmoji?: string;
}

interface LeaderboardProps {
  limit?: number;
  showUserRank?: boolean;
}

const Leaderboard: React.FC<LeaderboardProps> = ({ limit = 10, showUserRank = true }) => {
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [userRank, setUserRank] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'global' | 'category'>('global');
  const [selectedCategory, setSelectedCategory] = useState<string>('COFFEE_CHAT');

  useEffect(() => {
    fetchLeaderboard();
    if (showUserRank) {
      fetchUserRank();
    }
  }, [limit, showUserRank]);

  const fetchLeaderboard = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const response = await axios.get(`/api/gamification/leaderboard?limit=${limit}`, { headers });
      setLeaderboard(response.data);
    } catch (err) {
      setError('Failed to load leaderboard');
      console.error('Error fetching leaderboard:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchUserRank = async () => {
    try {
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const response = await axios.get('/api/gamification/user-rank', { headers });
      setUserRank(response.data);
    } catch (err) {
      console.error('Error fetching user rank:', err);
    }
  };

  const fetchCategoryLeaderboard = async (category: string) => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const response = await axios.get(`/api/gamification/leaderboard/category/${category}?limit=${limit}`, { headers });
      setLeaderboard(response.data);
    } catch (err) {
      setError('Failed to load category leaderboard');
      console.error('Error fetching category leaderboard:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryChange = (category: string) => {
    setSelectedCategory(category);
    if (activeTab === 'category') {
      fetchCategoryLeaderboard(category);
    }
  };

  const handleTabChange = (tab: 'global' | 'category') => {
    setActiveTab(tab);
    if (tab === 'global') {
      fetchLeaderboard();
    } else {
      fetchCategoryLeaderboard(selectedCategory);
    }
  };

  if (loading) {
    return (
      <div className="leaderboard-loading">
        <div className="loading-spinner"></div>
        <p>Loading leaderboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="leaderboard-error">
        <span className="error-icon">⚠️</span>
        <p>{error}</p>
        <button onClick={fetchLeaderboard} className="retry-btn">Retry</button>
      </div>
    );
  }

  return (
    <div className="leaderboard">
      <div className="leaderboard-header">
        <h3>🏆 Leaderboard</h3>
        <div className="leaderboard-tabs">
          <button
            className={`tab ${activeTab === 'global' ? 'active' : ''}`}
            onClick={() => handleTabChange('global')}
          >
            Global
          </button>
          <button
            className={`tab ${activeTab === 'category' ? 'active' : ''}`}
            onClick={() => handleTabChange('category')}
          >
            By Category
          </button>
        </div>
      </div>

      {activeTab === 'category' && (
        <div className="category-selector">
          <label htmlFor="category">Category:</label>
          <select
            id="category"
            value={selectedCategory}
            onChange={(e) => handleCategoryChange(e.target.value)}
          >
            <option value="COFFEE_CHAT">Coffee Chats</option>
            <option value="LOUNGE">Lounge Participation</option>
            <option value="LOGIN">Daily Login</option>
            <option value="NETWORKING">Networking</option>
            <option value="ENGAGEMENT">Engagement</option>
          </select>
        </div>
      )}

      {showUserRank && userRank && (
        <div className="user-rank-info">
          <span className="rank-label">Your Rank:</span>
          <span className="user-rank">#{userRank}</span>
        </div>
      )}

      <div className="leaderboard-list">
        {leaderboard.length === 0 ? (
          <div className="empty-leaderboard">
            <span className="empty-icon">📊</span>
            <p>No leaderboard data available</p>
          </div>
        ) : (
          leaderboard.map((entry) => (
            <div key={entry.userId} className={`leaderboard-entry ${entry.rankClass}`}>
              <div className="rank-info">
                <span className="rank-number">{entry.rank}</span>
                <span className="rank-emoji">{entry.rankEmoji}</span>
              </div>
              
              <div className="user-info">
                <div className="user-name">{entry.userName}</div>
                <div className="user-email">{entry.userEmail}</div>
              </div>
              
              <div className="stats">
                <div className="stat">
                  <span className="stat-value">{entry.totalPoints}</span>
                  <span className="stat-label">Points</span>
                </div>
                <div className="stat">
                  <span className="stat-value">{entry.totalBadges}</span>
                  <span className="stat-label">Badges</span>
                </div>
                <div className="stat">
                  <span className="stat-value">{entry.longestStreak}</span>
                  <span className="stat-label">Best Streak</span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <div className="leaderboard-footer">
        <p>Updated in real-time • Top {limit} performers</p>
      </div>
    </div>
  );
};

export default Leaderboard;
