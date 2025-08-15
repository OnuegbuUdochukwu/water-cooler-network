package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamificationSummaryDTO {
    private Long userId;
    private List<UserStreakDTO> activeStreaks;
    private List<UserBadgeDTO> recentBadges;
    private Long totalBadges;
    private Long totalPoints;
    private Integer longestStreak;
    private String longestStreakType;
    private List<UserBadgeDTO> displayedBadges;
    private boolean hasNewAchievements;
}
