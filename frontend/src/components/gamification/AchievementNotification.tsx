import React, { useEffect, useState } from 'react';
import './AchievementNotification.css';

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
}

interface AchievementNotificationProps {
    achievement: UserBadge | null;
    onClose: () => void;
    duration?: number;
}

const AchievementNotification: React.FC<AchievementNotificationProps> = ({ 
    achievement, 
    onClose, 
    duration = 5000 
}) => {
    const [isVisible, setIsVisible] = useState(false);
    const [isClosing, setIsClosing] = useState(false);

    useEffect(() => {
        if (achievement) {
            setIsVisible(true);
            const timer = setTimeout(() => {
                handleClose();
            }, duration);

            return () => clearTimeout(timer);
        }
    }, [achievement, duration]);

    const handleClose = () => {
        setIsClosing(true);
        setTimeout(() => {
            setIsVisible(false);
            setIsClosing(false);
            onClose();
        }, 300);
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

    const getRarityClass = (rarityLevel: number) => {
        switch (rarityLevel) {
            case 1: return 'rarity-common';
            case 2: return 'rarity-rare';
            case 3: return 'rarity-epic';
            case 4: return 'rarity-legendary';
            default: return 'rarity-common';
        }
    };

    if (!achievement || !isVisible) return null;

    return (
        <div className={`achievement-notification ${isClosing ? 'closing' : ''}`}>
            <div className="notification-overlay" onClick={handleClose}>
                <div className={`notification-card ${getRarityClass(achievement.badge.rarityLevel)}`} onClick={(e) => e.stopPropagation()}>
                    <button className="close-button" onClick={handleClose}>×</button>
                    
                    <div className="achievement-header">
                        <h2>🎉 Achievement Unlocked!</h2>
                    </div>
                    
                    <div className="badge-showcase">
                        <div className="badge-icon-large">
                            {achievement.badge.iconUrl ? (
                                <img 
                                    src={achievement.badge.iconUrl} 
                                    alt={achievement.badge.name}
                                    onError={(e) => {
                                        const target = e.target as HTMLImageElement;
                                        target.style.display = 'none';
                                        target.nextElementSibling!.textContent = getBadgeEmoji(achievement.badge.name);
                                    }}
                                />
                            ) : (
                                <span className="badge-emoji-large">{getBadgeEmoji(achievement.badge.name)}</span>
                            )}
                            <span className="badge-emoji-large" style={{ display: 'none' }}></span>
                        </div>
                        
                        <div className="badge-details">
                            <h3 className="badge-name">{achievement.badge.name}</h3>
                            <p className="badge-description">{achievement.badge.description}</p>
                            <span className="badge-rarity">{achievement.badge.rarityDisplay}</span>
                        </div>
                    </div>
                    
                    <div className="celebration-effects">
                        <div className="sparkle sparkle-1">✨</div>
                        <div className="sparkle sparkle-2">⭐</div>
                        <div className="sparkle sparkle-3">🌟</div>
                        <div className="sparkle sparkle-4">✨</div>
                        <div className="sparkle sparkle-5">⭐</div>
                        <div className="sparkle sparkle-6">🌟</div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AchievementNotification;
