package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.BadgeDTO;
import com.codewithudo.backend.dto.BadgeProgressDTO;
import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeService {
    
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserStreakRepository userStreakRepository;
    
    @Transactional
    public void checkAndAwardBadges(Long userId, ActivityLog.ActivityType activityType) {
        List<Badge> availableBadges = badgeRepository.findByIsActiveTrue();
        
        for (Badge badge : availableBadges) {
            if (!userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
                if (checkBadgeCriteria(userId, badge, activityType)) {
                    awardBadge(userId, badge);
                }
            }
        }
    }
    
    @Transactional
    public void awardBadge(Long userId, Badge badge) {
        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadgeId(badge.getId());
        userBadge.setCurrentProgress(badge.getRequiredCount() != null ? badge.getRequiredCount() : 100);
        userBadge.setIsDisplayed(true);
        userBadge.setNotificationSent(false);
        
        userBadgeRepository.save(userBadge);
        
        // Log badge earning activity
        ActivityLog badgeLog = new ActivityLog();
        badgeLog.setUserId(userId);
        badgeLog.setActivityType(ActivityLog.ActivityType.BADGE_EARNED);
        badgeLog.setEntityId(badge.getId());
        badgeLog.setActivityData("{\"badgeName\":\"" + badge.getName() + "\"}");
        badgeLog.setPointsEarned(50);
        
        activityLogRepository.save(badgeLog);
        
        log.info("Badge '{}' awarded to user {}", badge.getName(), userId);
    }
    
    private boolean checkBadgeCriteria(Long userId, Badge badge, ActivityLog.ActivityType activityType) {
        return switch (badge.getBadgeCategory()) {
            case LOGIN -> checkLoginBadges(userId, badge);
            case COFFEE_CHAT -> checkCoffeeChatBadges(userId, badge);
            case LOUNGE -> checkLoungeBadges(userId, badge);
            case NETWORKING -> checkNetworkingBadges(userId, badge);
            case ENGAGEMENT -> checkEngagementBadges(userId, badge);
            case LEADERSHIP -> checkLeadershipBadges(userId, badge);
        };
    }
    
    private boolean checkLoginBadges(Long userId, Badge badge) {
        if (badge.getName().equals("First Login")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOGIN) >= 1;
        }
        if (badge.getName().equals("Consistent User")) {
            return userStreakRepository.findByUserIdAndStreakType(userId, UserStreak.StreakType.DAILY_LOGIN)
                    .map(streak -> streak.getCurrentCount() >= 7)
                    .orElse(false);
        }
        if (badge.getName().equals("Login Champion")) {
            return userStreakRepository.findByUserIdAndStreakType(userId, UserStreak.StreakType.DAILY_LOGIN)
                    .map(streak -> streak.getCurrentCount() >= 30)
                    .orElse(false);
        }
        return false;
    }
    
    private boolean checkCoffeeChatBadges(Long userId, Badge badge) {
        if (badge.getName().equals("First Coffee Chat")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED) >= 1;
        }
        if (badge.getName().equals("Coffee Enthusiast")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED) >= 5;
        }
        if (badge.getName().equals("Networking Pro")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED) >= 20;
        }
        return false;
    }
    
    private boolean checkLoungeBadges(Long userId, Badge badge) {
        if (badge.getName().equals("Lounge Explorer")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_JOINED) >= 3;
        }
        if (badge.getName().equals("Conversation Starter")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_CREATED) >= 1;
        }
        if (badge.getName().equals("Community Builder")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_CREATED) >= 5;
        }
        return false;
    }
    
    private boolean checkNetworkingBadges(Long userId, Badge badge) {
        if (badge.getName().equals("Well Connected")) {
            long totalConnections = activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_ACCEPTED);
            return totalConnections >= 10;
        }
        return false;
    }
    
    private boolean checkEngagementBadges(Long userId, Badge badge) {
        if (badge.getName().equals("Active Participant")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_MESSAGE_SENT) >= 50;
        }
        if (badge.getName().equals("Super Engaged")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_MESSAGE_SENT) >= 200;
        }
        return false;
    }
    
    private boolean checkLeadershipBadges(Long userId, Badge badge) {
        if (badge.getName().equals("Mentor")) {
            // This would be implemented when mentorship feature is added
            return false;
        }
        return false;
    }
    
    /**
     * Initialize default badges if none exist
     */
    @Transactional
    public void initializeDefaultBadges() {
        if (badgeRepository.count() > 0) {
            return; // Badges already exist
        }
        
        log.info("Initializing default badges");
        
        // Login badges
        createBadge("First Login", "Welcome to Water Cooler Network!", "LOGIN", "🎉", 1, 1, 0);
        createBadge("Consistent User", "Logged in for 7 consecutive days", "LOGIN", "📅", 1, 7, 7);
        createBadge("Login Champion", "Logged in for 30 consecutive days", "LOGIN", "🔥", 2, 30, 30);
        createBadge("Loyalty Master", "Logged in for 100 consecutive days", "LOGIN", "💎", 3, 100, 100);
        
        // Coffee Chat badges
        createBadge("First Coffee Chat", "Completed your first coffee chat", "COFFEE_CHAT", "☕", 1, 1, 1);
        createBadge("Coffee Enthusiast", "Completed 5 coffee chats", "COFFEE_CHAT", "💬", 2, 5, 5);
        createBadge("Networking Pro", "Completed 25 coffee chats", "COFFEE_CHAT", "🤝", 3, 25, 25);
        createBadge("Social Butterfly", "Completed 50 coffee chats", "COFFEE_CHAT", "🦋", 4, 50, 50);
        
        // Lounge badges
        createBadge("Lounge Explorer", "Joined your first topic lounge", "LOUNGE", "🏠", 1, 1, 1);
        createBadge("Discussion Leader", "Created your first topic lounge", "LOUNGE", "👑", 2, 1, 1);
        createBadge("Community Builder", "Created 5 topic lounges", "LOUNGE", "🏗️", 3, 5, 5);
        createBadge("Lounge Master", "Created 10 topic lounges", "LOUNGE", "🎭", 4, 10, 10);
        
        // Networking badges
        createBadge("First Connection", "Made your first match", "NETWORKING", "🔗", 1, 1, 1);
        createBadge("Network Builder", "Made 10 matches", "NETWORKING", "🌐", 2, 10, 10);
        createBadge("Connection Master", "Made 50 matches", "NETWORKING", "🌟", 3, 50, 50);
        
        // Engagement badges
        createBadge("Active Participant", "Sent 100 lounge messages", "ENGAGEMENT", "💭", 2, 100, 100);
        createBadge("Conversation Starter", "Started 20 discussions", "ENGAGEMENT", "🎯", 3, 20, 20);
        createBadge("Community Pillar", "Earned 1000 total points", "ENGAGEMENT", "🏛️", 4, 1000, 1000);
        
        // Leadership badges
        createBadge("Mentor", "Helped 5 new users", "LEADERSHIP", "🎓", 3, 5, 5);
        createBadge("Team Captain", "Led 3 successful events", "LEADERSHIP", "⚡", 4, 3, 3);
        createBadge("Visionary", "Created innovative features", "LEADERSHIP", "🚀", 4, 1, 1);
        
        log.info("Default badges initialized successfully");
    }
    
    /**
     * Create a new badge
     */
    private void createBadge(String name, String description, String category, String iconUrl, 
                           Integer rarityLevel, Integer requiredCount, Integer points) {
        Badge badge = new Badge();
        badge.setName(name);
        badge.setDescription(description);
        badge.setBadgeCategory(Badge.BadgeCategory.valueOf(category));
        badge.setIconUrl(iconUrl);
        badge.setRarityLevel(rarityLevel);
        badge.setRequiredCount(requiredCount);
        badge.setIsActive(true);
        badge.setBadgeType(Badge.BadgeType.ACHIEVEMENT);
        
        badgeRepository.save(badge);
    }
    
    /**
     * Get all available badges
     */
    public List<BadgeDTO> getAllAvailableBadges() {
        return badgeRepository.findByIsActiveTrue()
                .stream()
                .map(BadgeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Get badges by category
     */
    public List<BadgeDTO> getBadgesByCategory(Badge.BadgeCategory category) {
        return badgeRepository.findByBadgeCategoryAndIsActiveTrue(category)
                .stream()
                .map(BadgeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Get badges by rarity level
     */
    public List<BadgeDTO> getBadgesByRarity(Integer rarityLevel) {
        return badgeRepository.findByRarityLevelAndIsActiveTrue(rarityLevel)
                .stream()
                .map(BadgeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Toggle badge display for user
     */
    @Transactional
    public void toggleBadgeDisplay(Long userId, Long badgeId) {
        Optional<UserBadge> userBadge = userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId);
        if (userBadge.isPresent()) {
            UserBadge ub = userBadge.get();
            ub.setIsDisplayed(!ub.getIsDisplayed());
            userBadgeRepository.save(ub);
        }
    }
    
    /**
     * Get user's progress towards a specific badge
     */
    public BadgeProgressDTO getBadgeProgress(Long userId, Long badgeId) {
        Optional<Badge> badge = badgeRepository.findById(badgeId);
        if (badge.isEmpty()) {
            return null;
        }
        
        Badge b = badge.get();
        Optional<UserBadge> userBadge = userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId);
        
        BadgeProgressDTO progress = new BadgeProgressDTO();
        progress.setBadgeId(badgeId);
        progress.setBadgeName(b.getName());
        progress.setBadgeDescription(b.getDescription());
        progress.setIconUrl(b.getIconUrl());
        progress.setRequiredCount(b.getRequiredCount());
        progress.setRarityLevel(b.getRarityLevel());
        progress.setIsEarned(userBadge.isPresent());
        
        if (userBadge.isPresent()) {
            progress.setCurrentProgress(userBadge.get().getCurrentProgress());
            progress.setEarnedAt(userBadge.get().getEarnedAt());
        } else {
            progress.setCurrentProgress(calculateCurrentProgress(userId, b));
        }
        
        return progress;
    }
    
    /**
     * Calculate current progress towards a badge
     */
    private Integer calculateCurrentProgress(Long userId, Badge badge) {
        return switch (badge.getBadgeCategory()) {
            case LOGIN -> calculateLoginProgress(userId, badge);
            case COFFEE_CHAT -> calculateCoffeeChatProgress(userId, badge);
            case LOUNGE -> calculateLoungeProgress(userId, badge);
            case NETWORKING -> calculateNetworkingProgress(userId, badge);
            case ENGAGEMENT -> calculateEngagementProgress(userId, badge);
            case LEADERSHIP -> calculateLeadershipProgress(userId, badge);
        };
    }
    
    private Integer calculateLoginProgress(Long userId, Badge badge) {
        if (badge.getName().equals("First Login")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOGIN).intValue();
        }
        if (badge.getName().equals("Consistent User")) {
            return userStreakRepository.findByUserIdAndStreakType(userId, UserStreak.StreakType.DAILY_LOGIN)
                    .map(streak -> Math.min(streak.getCurrentCount(), 7))
                    .orElse(0);
        }
        if (badge.getName().equals("Login Champion")) {
            return userStreakRepository.findByUserIdAndStreakType(userId, UserStreak.StreakType.DAILY_LOGIN)
                    .map(streak -> Math.min(streak.getCurrentCount(), 30))
                    .orElse(0);
        }
        if (badge.getName().equals("Loyalty Master")) {
            return userStreakRepository.findByUserIdAndStreakType(userId, UserStreak.StreakType.DAILY_LOGIN)
                    .map(streak -> Math.min(streak.getCurrentCount(), 100))
                    .orElse(0);
        }
        return 0;
    }
    
    private Integer calculateCoffeeChatProgress(Long userId, Badge badge) {
        if (badge.getName().equals("First Coffee Chat")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED).intValue();
        }
        if (badge.getName().equals("Coffee Enthusiast")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED).intValue(), 5);
        }
        if (badge.getName().equals("Networking Pro")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED).intValue(), 25);
        }
        if (badge.getName().equals("Social Butterfly")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.COFFEE_CHAT_COMPLETED).intValue(), 50);
        }
        return 0;
    }
    
    private Integer calculateLoungeProgress(Long userId, Badge badge) {
        if (badge.getName().equals("Lounge Explorer")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_JOINED).intValue();
        }
        if (badge.getName().equals("Discussion Leader")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_CREATED).intValue();
        }
        if (badge.getName().equals("Community Builder")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_CREATED).intValue(), 5);
        }
        if (badge.getName().equals("Lounge Master")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_CREATED).intValue(), 10);
        }
        return 0;
    }
    
    private Integer calculateNetworkingProgress(Long userId, Badge badge) {
        if (badge.getName().equals("First Connection")) {
            return activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.MATCH_FOUND).intValue();
        }
        if (badge.getName().equals("Network Builder")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.MATCH_FOUND).intValue(), 10);
        }
        if (badge.getName().equals("Connection Master")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.MATCH_FOUND).intValue(), 50);
        }
        return 0;
    }
    
    private Integer calculateEngagementProgress(Long userId, Badge badge) {
        if (badge.getName().equals("Active Participant")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_MESSAGE_SENT).intValue(), 100);
        }
        if (badge.getName().equals("Conversation Starter")) {
            return Math.min(activityLogRepository.countByUserIdAndActivityType(userId, ActivityLog.ActivityType.LOUNGE_CREATED).intValue(), 20);
        }
        if (badge.getName().equals("Community Pillar")) {
            Long totalPoints = activityLogRepository.getTotalPointsByUserId(userId);
            return totalPoints != null ? Math.min(totalPoints.intValue(), 1000) : 0;
        }
        return 0;
    }
    
    private Integer calculateLeadershipProgress(Long userId, Badge badge) {
        // These would need more complex logic based on specific leadership activities
        // For now, return 0 as placeholder
        return 0;
    }
}
