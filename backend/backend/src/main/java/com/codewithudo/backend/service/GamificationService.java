package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.*;
import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {
    
    private final UserStreakRepository userStreakRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ActivityLogRepository activityLogRepository;
    private final BadgeService badgeService;
    
    @Transactional
    public void logActivity(Long userId, ActivityLog.ActivityType activityType, Long entityId, String activityData) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUserId(userId);
        activityLog.setActivityType(activityType);
        activityLog.setEntityId(entityId);
        activityLog.setActivityData(activityData);
        activityLog.setPointsEarned(calculatePoints(activityType));
        
        activityLogRepository.save(activityLog);
        
        // Update streaks based on activity
        updateStreaksForActivity(userId, activityType);
        
        // Check for new badge achievements
        badgeService.checkAndAwardBadges(userId, activityType);
        
        log.info("Activity logged for user {}: {} with {} points", userId, activityType, activityLog.getPointsEarned());
    }
    
    @Transactional
    public void updateStreaksForActivity(Long userId, ActivityLog.ActivityType activityType) {
        UserStreak.StreakType streakType = mapActivityToStreakType(activityType);
        if (streakType == null) return;
        
        Optional<UserStreak> existingStreak = userStreakRepository.findByUserIdAndStreakType(userId, streakType);
        UserStreak streak;
        
        if (existingStreak.isPresent()) {
            streak = existingStreak.get();
            if (shouldIncrementStreak(streak, activityType)) {
                streak.incrementStreak();
            }
        } else {
            streak = new UserStreak();
            streak.setUserId(userId);
            streak.setStreakType(streakType);
            streak.incrementStreak();
        }
        
        userStreakRepository.save(streak);
    }
    
    public GamificationSummaryDTO getUserGamificationSummary(Long userId) {
        List<UserStreak> activeStreaks = userStreakRepository.findActiveStreaksByUserId(userId);
        List<UserBadge> recentBadges = userBadgeRepository.findByUserIdWithBadge(userId)
                .stream()
                .sorted((a, b) -> b.getEarnedAt().compareTo(a.getEarnedAt()))
                .limit(5)
                .collect(Collectors.toList());
        
        List<UserBadge> displayedBadges = userBadgeRepository.findDisplayedBadgesByUserId(userId);
        Long totalBadges = userBadgeRepository.countBadgesByUserId(userId);
        Long totalPoints = activityLogRepository.getTotalPointsByUserId(userId);
        
        // Find longest streak
        Optional<UserStreak> longestStreak = userStreakRepository.findByUserId(userId)
                .stream()
                .max((a, b) -> Integer.compare(a.getBestCount(), b.getBestCount()));
        
        GamificationSummaryDTO summary = new GamificationSummaryDTO();
        summary.setUserId(userId);
        summary.setActiveStreaks(activeStreaks.stream().map(UserStreakDTO::fromEntity).collect(Collectors.toList()));
        summary.setRecentBadges(recentBadges.stream().map(UserBadgeDTO::fromEntity).collect(Collectors.toList()));
        summary.setDisplayedBadges(displayedBadges.stream().map(UserBadgeDTO::fromEntity).collect(Collectors.toList()));
        summary.setTotalBadges(totalBadges != null ? totalBadges : 0L);
        summary.setTotalPoints(totalPoints != null ? totalPoints : 0L);
        
        if (longestStreak.isPresent()) {
            summary.setLongestStreak(longestStreak.get().getBestCount());
            summary.setLongestStreakType(longestStreak.get().getStreakType().toString());
        } else {
            summary.setLongestStreak(0);
            summary.setLongestStreakType("None");
        }
        
        // Check for new achievements
        summary.setHasNewAchievements(userBadgeRepository.findUnnotifiedBadges()
                .stream()
                .anyMatch(ub -> ub.getUserId().equals(userId)));
        
        return summary;
    }
    
    public List<UserStreakDTO> getUserStreaks(Long userId) {
        return userStreakRepository.findByUserId(userId)
                .stream()
                .map(UserStreakDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<UserBadgeDTO> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserIdWithBadge(userId)
                .stream()
                .map(UserBadgeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Get leaderboard entries
     */
    public List<LeaderboardEntryDTO> getLeaderboard(int limit) {
        List<Object[]> leaderboardData = activityLogRepository.getLeaderboardData(limit);
        
        return leaderboardData.stream()
                .map(data -> {
                    LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
                    entry.setUserId((Long) data[0]);
                    entry.setUserName((String) data[1]);
                    entry.setUserEmail((String) data[2]);
                    entry.setTotalPoints((Integer) data[3]);
                    entry.setTotalBadges((Long) data[4] != null ? ((Long) data[4]).intValue() : 0);
                    entry.setLongestStreak((Integer) data[5]);
                    entry.setLongestStreakType((String) data[6]);
                    entry.setRank((Integer) data[7]);
                    return entry;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's ranking
     */
    public Integer getUserRank(Long userId) {
        return activityLogRepository.getUserRank(userId);
    }
    
    /**
     * Get top performers by category
     */
    public List<LeaderboardEntryDTO> getTopPerformersByCategory(String category, int limit) {
        List<Object[]> topData = activityLogRepository.getTopPerformersByCategory(category, limit);
        
        return topData.stream()
                .map(data -> {
                    LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
                    entry.setUserId((Long) data[0]);
                    entry.setUserName((String) data[1]);
                    entry.setTotalPoints((Integer) data[2]);
                    entry.setRank((Integer) data[3]);
                    return entry;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void resetInactiveStreaks() {
        List<UserStreak> allStreaks = userStreakRepository.findAll();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        for (UserStreak streak : allStreaks) {
            if (streak.getLastActivityDate() != null && 
                streak.getLastActivityDate().isBefore(yesterday) && 
                streak.getCurrentCount() > 0) {
                streak.resetStreak();
                userStreakRepository.save(streak);
                log.info("Reset inactive streak for user {}: {}", streak.getUserId(), streak.getStreakType());
            }
        }
    }
    
    private UserStreak.StreakType mapActivityToStreakType(ActivityLog.ActivityType activityType) {
        return switch (activityType) {
            case LOGIN -> UserStreak.StreakType.DAILY_LOGIN;
            case COFFEE_CHAT_COMPLETED -> UserStreak.StreakType.COFFEE_CHAT;
            case LOUNGE_JOINED, LOUNGE_MESSAGE_SENT -> UserStreak.StreakType.LOUNGE_PARTICIPATION;
            default -> null;
        };
    }
    
    private boolean shouldIncrementStreak(UserStreak streak, ActivityLog.ActivityType activityType) {
        if (streak.getLastActivityDate() == null) return true;
        
        LocalDate today = LocalDate.now();
        LocalDate lastActivity = streak.getLastActivityDate();
        
        // For daily activities, only increment once per day
        if (activityType == ActivityLog.ActivityType.LOGIN) {
            return !lastActivity.equals(today);
        }
        
        // For other activities, check if it's been at least a day
        return lastActivity.isBefore(today);
    }
    
    private Integer calculatePoints(ActivityLog.ActivityType activityType) {
        return switch (activityType) {
            case LOGIN -> 5;
            case COFFEE_CHAT_REQUEST -> 2;
            case COFFEE_CHAT_ACCEPTED -> 5;
            case COFFEE_CHAT_COMPLETED -> 20;
            case LOUNGE_JOINED -> 3;
            case LOUNGE_CREATED -> 15;
            case LOUNGE_MESSAGE_SENT -> 1;
            case PROFILE_UPDATED -> 5;
            case MATCH_FOUND -> 10;
            case BADGE_EARNED -> 50;
        };
    }
}
