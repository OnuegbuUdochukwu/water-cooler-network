import React from 'react';
import './BadgeDisplay.css';

interface Badge {
    id: number;
    name: string;
    description: string;
    iconUrl: string;
    rarityLevel: number;
    rarityDisplay: string;
}

interface UserBadge {
    id: number;
    badge: Badge;
    earnedAt: string;
    currentProgress: number;
    isDisplayed: boolean;
    progressPercentage: string;
}

interface BadgeDisplayProps {
    badges: UserBadge[];
    showProgress?: boolean;
    maxDisplay?: number;
    size?: 'small' | 'medium' | 'large';
}

const BadgeDisplay: React.FC<BadgeDisplayProps> = ({ 
    badges, 
    showProgress = false, 
    maxDisplay = 10,
    size = 'medium'
}) => {
    const displayedBadges = badges.slice(0, maxDisplay);

    const getRarityClass = (rarityLevel: number) => {
        switch (rarityLevel) {
            case 1: return 'rarity-common';
            case 2: return 'rarity-rare';
            case 3: return 'rarity-epic';
            case 4: return 'rarity-legendary';
            default: return 'rarity-common';
        }
    };

    return (
        <div className={`badge-display ${size}`}>
            {displayedBadges.map(userBadge => (
                <div 
                    key={userBadge.id} 
                    className={`badge-item ${getRarityClass(userBadge.badge.rarityLevel)}`}
                    title={`${userBadge.badge.name} - ${userBadge.badge.description}`}
                >
                    <div className="badge-icon">
                        {userBadge.badge.iconUrl ? (
                            <img 
                                src={userBadge.badge.iconUrl} 
                                alt={userBadge.badge.name}
                                onError={(e) => {
                                    // Fallback to emoji for missing icons
                                    const target = e.target as HTMLImageElement;
                                    target.style.display = 'none';
                                    target.nextElementSibling!.textContent = getBadgeEmoji(userBadge.badge.name);
                                }}
                            />
                        ) : (
                            <span className="badge-emoji">{getBadgeEmoji(userBadge.badge.name)}</span>
                        )}
                        <span className="badge-emoji" style={{ display: 'none' }}></span>
                    </div>
                    
                    <div className="badge-info">
                        <span className="badge-name">{userBadge.badge.name}</span>
                        <span className="badge-rarity">{userBadge.badge.rarityDisplay}</span>
                        {showProgress && (
                            <div className="progress-bar">
                                <div 
                                    className="progress-fill"
                                    style={{ width: userBadge.progressPercentage }}
                                ></div>
                            </div>
                        )}
                    </div>
                </div>
            ))}
            
            {badges.length > maxDisplay && (
                <div className="badge-item more-badges">
                    <span>+{badges.length - maxDisplay} more</span>
                </div>
            )}
        </div>
    );
};

const getBadgeEmoji = (badgeName: string): string => {
    const emojiMap: { [key: string]: string } = {
        'First Login': '🚪',
        'Consistent User': '📅',
        'Login Champion': '👑',
        'First Coffee Chat': '☕',
        'Coffee Enthusiast': '☕',
        'Networking Pro': '🤝',
        'Lounge Explorer': '🔍',
        'Conversation Starter': '💬',
        'Community Builder': '🏗️',
        'Active Participant': '🎯',
        'Super Engaged': '⚡',
        'Well Connected': '🌐'
    };
    
    return emojiMap[badgeName] || '🏆';
};

export default BadgeDisplay;
