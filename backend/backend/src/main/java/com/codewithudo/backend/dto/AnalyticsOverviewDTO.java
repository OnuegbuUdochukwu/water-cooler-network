package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsOverviewDTO {
    
    // Platform Summary
    private Long totalUsers;
    private Long activeUsersToday;
    private Long newUsersToday;
    private Double userGrowthRate;
    
    // Match Summary
    private Long totalMatches;
    private Long matchesCreatedToday;
    private Double matchSuccessRate;
    private Long matchesCompletedToday;
    
    // Meeting Summary
    private Long meetingsScheduledToday;
    private Long meetingsCompletedToday;
    private Double meetingCompletionRate;
    private Double averageMeetingDuration;
    
    // Engagement Summary
    private Long totalLounges;
    private Long activeLoungestoday;
    private Long messagesSentToday;
    private Double averageFeedbackRating;
    
    // Trend Data
    private List<DailyMetricDTO> userGrowthTrend;
    private List<DailyMetricDTO> matchSuccessTrend;
    private List<DailyMetricDTO> engagementTrend;
    
    // Top Performers
    private List<TopUserDTO> mostActiveUsers;
    private List<TopUserDTO> topRatedUsers;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetricDTO {
        private LocalDate date;
        private Double value;
        private String label;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUserDTO {
        private Long userId;
        private String userName;
        private Double score;
        private String metric;
    }
}
