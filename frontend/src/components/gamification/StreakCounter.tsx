import React from 'react';
import './StreakCounter.css';

interface UserStreak {
    id: number;
    streakType: string;
    streakTypeDisplay: string;
    currentCount: number;
    bestCount: number;
    isActive: boolean;
    lastActivityDate: string;
}

interface StreakCounterProps {
    streaks: UserStreak[];
    compact?: boolean;
}

const StreakCounter: React.FC<StreakCounterProps> = ({ streaks, compact = false }) => {
    const getStreakIcon = (streakType: string): string => {
        const iconMap: { [key: string]: string } = {
            'DAILY_LOGIN': '🔥',
            'COFFEE_CHAT': '☕',
            'LOUNGE_PARTICIPATION': '💬',
            'MESSAGE_STREAK': '📝'
        };
        return iconMap[streakType] || '⭐';
    };

    const getStreakColor = (count: number): string => {
        if (count >= 30) return 'streak-legendary';
        if (count >= 14) return 'streak-epic';
        if (count >= 7) return 'streak-rare';
        if (count >= 3) return 'streak-common';
        return 'streak-none';
    };

    if (compact) {
        const activeStreaks = streaks.filter(streak => streak.isActive && streak.currentCount > 0);
        const totalActiveStreaks = activeStreaks.length;
        const longestStreak = Math.max(...streaks.map(s => s.currentCount), 0);

        return (
            <div className="streak-counter compact">
                <div className="streak-summary">
                    <span className="streak-icon">🔥</span>
                    <div className="streak-info">
                        <span className="streak-count">{longestStreak}</span>
                        <span className="streak-label">Best Streak</span>
                    </div>
                    {totalActiveStreaks > 0 && (
                        <div className="active-streaks">
                            <span className="active-count">{totalActiveStreaks}</span>
                            <span className="active-label">Active</span>
                        </div>
                    )}
                </div>
            </div>
        );
    }

    return (
        <div className="streak-counter">
            <h3 className="streak-title">Your Streaks</h3>
            <div className="streak-grid">
                {streaks.map(streak => (
                    <div 
                        key={streak.id} 
                        className={`streak-item ${getStreakColor(streak.currentCount)} ${!streak.isActive ? 'inactive' : ''}`}
                    >
                        <div className="streak-header">
                            <span className="streak-icon">{getStreakIcon(streak.streakType)}</span>
                            <span className="streak-type">{streak.streakTypeDisplay}</span>
                        </div>
                        
                        <div className="streak-numbers">
                            <div className="current-streak">
                                <span className="number">{streak.currentCount}</span>
                                <span className="label">Current</span>
                            </div>
                            <div className="best-streak">
                                <span className="number">{streak.bestCount}</span>
                                <span className="label">Best</span>
                            </div>
                        </div>
                        
                        <div className="streak-status">
                            {streak.isActive ? (
                                <span className="status active">🔥 Active</span>
                            ) : (
                                <span className="status inactive">💤 Inactive</span>
                            )}
                        </div>
                        
                        {streak.currentCount > 0 && (
                            <div className="streak-progress">
                                <div className="progress-bar">
                                    <div 
                                        className="progress-fill"
                                        style={{ 
                                            width: `${Math.min((streak.currentCount / Math.max(streak.bestCount, 7)) * 100, 100)}%` 
                                        }}
                                    ></div>
                                </div>
                            </div>
                        )}
                    </div>
                ))}
                
                {streaks.length === 0 && (
                    <div className="no-streaks">
                        <span className="no-streaks-icon">🎯</span>
                        <p>Start building your streaks!</p>
                        <p className="no-streaks-hint">Login daily, join coffee chats, and participate in lounges to build streaks.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default StreakCounter;
