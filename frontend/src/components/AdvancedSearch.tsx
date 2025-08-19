import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import './AdvancedSearch.css';

interface User {
  id: number;
  name: string;
  email: string;
  industry: string;
  skills: string;
  interests: string;
  role: string;
  linkedinUrl?: string;
  companyId?: number;
  active: boolean;
}

interface SearchResult {
  users: User[];
  totalUsers: number;
  totalPages: number;
  currentPage: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

const AdvancedSearch: React.FC = () => {
  const [query, setQuery] = useState('');
  const [industry, setIndustry] = useState('');
  const [skills, setSkills] = useState('');
  const [location, setLocation] = useState('');
  const [results, setResults] = useState<SearchResult | null>(null);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [popularSearches, setPopularSearches] = useState<string[]>([]);
  const [recentSearches, setRecentSearches] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const searchInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    loadPopularSearches();
    loadRecentSearches();
  }, []);

  useEffect(() => {
    if (query.length > 2) {
      loadSuggestions();
    } else {
      setSuggestions([]);
    }
  }, [query]);

  const loadSuggestions = async () => {
    try {
      const response = await axios.get(`/api/search/suggestions?query=${query}&limit=5`);
      setSuggestions(response.data);
      setShowSuggestions(true);
    } catch (error) {
      console.error('Error loading suggestions:', error);
    }
  };

  const loadPopularSearches = async () => {
    try {
      const response = await axios.get('/api/search/popular?limit=10');
      setPopularSearches(response.data);
    } catch (error) {
      console.error('Error loading popular searches:', error);
    }
  };

  const loadRecentSearches = async () => {
    try {
      const response = await axios.get('/api/search/recent?limit=5');
      setRecentSearches(response.data);
    } catch (error) {
      console.error('Error loading recent searches:', error);
    }
  };

  const handleSearch = async (page: number = 0) => {
    if (!query.trim()) return;

    setLoading(true);
    setShowSuggestions(false);
    
    try {
      const params = new URLSearchParams({
        query: query.trim(),
        page: page.toString(),
        size: '10'
      });

      if (industry) params.append('industry', industry);
      if (skills) params.append('skills', skills);
      if (location) params.append('location', location);

      const response = await axios.get(`/api/search/users?${params}`);
      setResults(response.data);
      setCurrentPage(page);
    } catch (error) {
      console.error('Error searching:', error);
      setResults(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSuggestionClick = (suggestion: string) => {
    setQuery(suggestion);
    setShowSuggestions(false);
    setTimeout(() => handleSearch(), 100);
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const clearFilters = () => {
    setIndustry('');
    setSkills('');
    setLocation('');
    if (query) {
      handleSearch();
    }
  };

  return (
    <div className="advanced-search">
      <div className="search-header">
        <h2>Advanced Search</h2>
        <p>Find colleagues, experts, and connections across the platform</p>
      </div>

      <div className="search-container">
        <div className="search-input-container">
          <input
            ref={searchInputRef}
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Search for people, skills, industries..."
            className="search-input"
            onFocus={() => setShowSuggestions(suggestions.length > 0)}
          />
          <button 
            onClick={() => handleSearch()} 
            disabled={!query.trim() || loading}
            className="search-button"
          >
            {loading ? 'Searching...' : 'Search'}
          </button>

          {showSuggestions && suggestions.length > 0 && (
            <div className="suggestions-dropdown">
              {suggestions.map((suggestion, index) => (
                <div
                  key={index}
                  className="suggestion-item"
                  onClick={() => handleSuggestionClick(suggestion)}
                >
                  {suggestion}
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="filters-container">
          <div className="filter-group">
            <label>Industry</label>
            <input
              type="text"
              value={industry}
              onChange={(e) => setIndustry(e.target.value)}
              placeholder="e.g., Technology, Healthcare"
              className="filter-input"
            />
          </div>
          <div className="filter-group">
            <label>Skills</label>
            <input
              type="text"
              value={skills}
              onChange={(e) => setSkills(e.target.value)}
              placeholder="e.g., React, Java, Leadership"
              className="filter-input"
            />
          </div>
          <div className="filter-group">
            <label>Location</label>
            <input
              type="text"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              placeholder="e.g., San Francisco, Remote"
              className="filter-input"
            />
          </div>
          <button onClick={clearFilters} className="clear-filters-button">
            Clear Filters
          </button>
        </div>
      </div>

      {!results && !loading && (
        <div className="search-suggestions">
          {popularSearches.length > 0 && (
            <div className="suggestion-section">
              <h3>Popular Searches</h3>
              <div className="suggestion-tags">
                {popularSearches.map((search, index) => (
                  <span
                    key={index}
                    className="suggestion-tag"
                    onClick={() => handleSuggestionClick(search)}
                  >
                    {search}
                  </span>
                ))}
              </div>
            </div>
          )}

          {recentSearches.length > 0 && (
            <div className="suggestion-section">
              <h3>Recent Searches</h3>
              <div className="suggestion-tags">
                {recentSearches.map((search, index) => (
                  <span
                    key={index}
                    className="suggestion-tag"
                    onClick={() => handleSuggestionClick(search)}
                  >
                    {search}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {loading && (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Searching...</p>
        </div>
      )}

      {results && (
        <div className="search-results">
          <div className="results-header">
            <h3>Search Results</h3>
            <p>{results.totalUsers} users found</p>
          </div>

          <div className="results-grid">
            {results.users.map((user) => (
              <div key={user.id} className="user-card">
                <div className="user-avatar">
                  {user.name.charAt(0).toUpperCase()}
                </div>
                <div className="user-info">
                  <h4>{user.name}</h4>
                  <p className="user-role">{user.role}</p>
                  {user.industry && <p className="user-industry">{user.industry}</p>}
                  {user.skills && (
                    <div className="user-skills">
                      {user.skills.split(',').slice(0, 3).map((skill, index) => (
                        <span key={index} className="skill-tag">
                          {skill.trim()}
                        </span>
                      ))}
                    </div>
                  )}
                  <div className="user-actions">
                    <button className="connect-button">Connect</button>
                    {user.linkedinUrl && (
                      <a 
                        href={user.linkedinUrl} 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="linkedin-link"
                      >
                        LinkedIn
                      </a>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>

          {results.totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => handleSearch(currentPage - 1)}
                disabled={!results.hasPrevious}
                className="pagination-button"
              >
                Previous
              </button>
              <span className="pagination-info">
                Page {currentPage + 1} of {results.totalPages}
              </span>
              <button
                onClick={() => handleSearch(currentPage + 1)}
                disabled={!results.hasNext}
                className="pagination-button"
              >
                Next
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default AdvancedSearch;
