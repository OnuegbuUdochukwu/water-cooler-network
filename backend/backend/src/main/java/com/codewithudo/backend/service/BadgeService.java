package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.BadgeDTO;
import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    
    public List<BadgeDTO> getAllAvailableBadges() {
        return badgeRepository.findAllActiveBadgesOrderedByRarity()
                .stream()
                .map(BadgeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public void initializeDefaultBadges() {
        if (badgeRepository.count() == 0) {
            createDefaultBadges();
        }
    }
    
    @Transactional
    public void createDefaultBadges() {
        // Login Badges
        createBadge("First Login", "Welcome to Water Cooler Network!", "/icons/first-login.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.LOGIN, 1, 1);
        createBadge("Consistent User", "Login for 7 consecutive days", "/icons/consistent-user.svg", 
                Badge.BadgeType.STREAK, Badge.BadgeCategory.LOGIN, 7, 2);
        createBadge("Login Champion", "Login for 30 consecutive days", "/icons/login-champion.svg", 
                Badge.BadgeType.STREAK, Badge.BadgeCategory.LOGIN, 30, 3);
        
        // Coffee Chat Badges
        createBadge("First Coffee Chat", "Complete your first coffee chat", "/icons/first-chat.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.COFFEE_CHAT, 1, 1);
        createBadge("Coffee Enthusiast", "Complete 5 coffee chats", "/icons/coffee-enthusiast.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.COFFEE_CHAT, 5, 2);
        createBadge("Networking Pro", "Complete 20 coffee chats", "/icons/networking-pro.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.COFFEE_CHAT, 20, 3);
        
        // Lounge Badges
        createBadge("Lounge Explorer", "Join 3 different lounges", "/icons/lounge-explorer.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.LOUNGE, 3, 1);
        createBadge("Conversation Starter", "Create your first lounge", "/icons/conversation-starter.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.LOUNGE, 1, 2);
        createBadge("Community Builder", "Create 5 lounges", "/icons/community-builder.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.LOUNGE, 5, 3);
        
        // Engagement Badges
        createBadge("Active Participant", "Send 50 messages in lounges", "/icons/active-participant.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.ENGAGEMENT, 50, 2);
        createBadge("Super Engaged", "Send 200 messages in lounges", "/icons/super-engaged.svg", 
                Badge.BadgeType.MILESTONE, Badge.BadgeCategory.ENGAGEMENT, 200, 3);
        
        // Networking Badges
        createBadge("Well Connected", "Accept 10 coffee chat invitations", "/icons/well-connected.svg", 
                Badge.BadgeType.SOCIAL, Badge.BadgeCategory.NETWORKING, 10, 2);
        
        log.info("Default badges created successfully");
    }
    
    private void createBadge(String name, String description, String iconUrl, 
                           Badge.BadgeType badgeType, Badge.BadgeCategory badgeCategory, 
                           Integer requiredCount, Integer rarityLevel) {
        Badge badge = new Badge();
        badge.setName(name);
        badge.setDescription(description);
        badge.setIconUrl(iconUrl);
        badge.setBadgeType(badgeType);
        badge.setBadgeCategory(badgeCategory);
        badge.setRequiredCount(requiredCount);
        badge.setRarityLevel(rarityLevel);
        badge.setIsActive(true);
        
        badgeRepository.save(badge);
    }
}
