package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInsightsDTO {
    
    private Long userId;
    private String userName;
    private LocalDate memberSince;
    
    // Activity Summary
    private Integer totalLogins;
    private Integer totalSessionMinutes;
    private Integer averageSessionDuration;
    private Integer totalActionsPerformed;
    
    // Matching Performance
    private Integer totalMatchesInitiated;
    private Integer totalMatchesReceived;
    private Integer totalMatchesAccepted;
    private Integer totalMatchesCompleted;
    private Double matchSuccessRate;
    private Double matchAcceptanceRate;
    
    // Communication Stats
    private Integer totalMessagesSent;
    private Integer totalMessagesReceived;
    private Integer conversationsStarted;
    private Integer averageResponseTime;
    
    // Meeting Performance
    private Integer totalMeetingsScheduled;
    private Integer totalMeetingsAttended;
    private Integer totalMeetingMinutes;
    private Double meetingAttendanceRate;
    
    // Community Engagement
    private Integer loungesJoined;
    private Integer loungeMessagesPosted;
    private Integer loungesCreated;
    private Double communityEngagementScore;
    
    // Feedback & Ratings
    private Integer feedbackGiven;
    private Integer feedbackReceived;
    private Double averageRatingGiven;
    private Double averageRatingReceived;
    private Double reputationScore;
    
    // Streaks & Achievements
    private Integer currentLoginStreak;
    private Integer longestLoginStreak;
    private Integer currentMatchStreak;
    private Integer totalBadgesEarned;
    
    // Trends (Last 30 days)
    private List<DailyActivityDTO> activityTrend;
    private List<WeeklyStatsDTO> weeklyStats;
    
    // Insights & Recommendations
    private List<String> insights;
    private List<String> recommendations;
    private String engagementLevel; // LOW, MEDIUM, HIGH, VERY_HIGH
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyActivityDTO {
        private LocalDate date;
        private Integer loginCount;
        private Integer actionsPerformed;
        private Integer matchesCreated;
        private Integer messagesExchanged;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyStatsDTO {
        private LocalDate weekStartDate;
        private Integer totalLogins;
        private Integer totalMatches;
        private Integer totalMeetings;
        private Double weeklyScore;
    }
}
