package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.AnalyticsOverviewDTO;
import com.codewithudo.backend.dto.UserInsightsDTO;
import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final PlatformAnalyticsRepository platformAnalyticsRepository;
    private final UserAnalyticsRepository userAnalyticsRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final ScheduledMeetingRepository scheduledMeetingRepository;
    // private final LoungeRepository loungeRepository;
    // private final LoungeMessageRepository loungeMessageRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final MatchFeedbackRepository matchFeedbackRepository;
    // private final UserStreakRepository userStreakRepository;
    
    @Transactional(readOnly = true)
    public AnalyticsOverviewDTO getPlatformOverview() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        
        // Get today's analytics
        Optional<PlatformAnalytics> todayAnalytics = platformAnalyticsRepository.findByDate(today);
        PlatformAnalytics analytics = todayAnalytics.orElse(new PlatformAnalytics());
        
        AnalyticsOverviewDTO overview = new AnalyticsOverviewDTO();
        
        // Platform Summary
        overview.setTotalUsers(analytics.getTotalUsers());
        overview.setActiveUsersToday(analytics.getActiveUsersToday());
        overview.setNewUsersToday(analytics.getNewUsersToday());
        overview.setUserGrowthRate(analytics.getUserGrowthRate());
        
        // Match Summary
        overview.setTotalMatches(analytics.getTotalMatches());
        overview.setMatchesCreatedToday(analytics.getMatchesCreatedToday());
        overview.setMatchSuccessRate(analytics.getMatchSuccessRate());
        overview.setMatchesCompletedToday(analytics.getMatchesCompletedToday());
        
        // Meeting Summary
        overview.setMeetingsScheduledToday(analytics.getMeetingsScheduledToday());
        overview.setMeetingsCompletedToday(analytics.getMeetingsCompletedToday());
        overview.setMeetingCompletionRate(analytics.getMeetingCompletionRate());
        overview.setAverageMeetingDuration(analytics.getAverageMeetingDuration());
        
        // Engagement Summary
        overview.setTotalLounges(analytics.getTotalLounges());
        overview.setActiveLoungestoday(analytics.getActiveLoungestoday());
        overview.setMessagesSentToday(analytics.getMessagesSentToday());
        overview.setAverageFeedbackRating(analytics.getAverageFeedbackRating());
        
        // Get trend data
        List<PlatformAnalytics> trendData = platformAnalyticsRepository.findByDateBetweenOrderByDateDesc(thirtyDaysAgo, today);
        overview.setUserGrowthTrend(createUserGrowthTrend(trendData));
        overview.setMatchSuccessTrend(createMatchSuccessTrend(trendData));
        overview.setEngagementTrend(createEngagementTrend(trendData));
        
        // Get top performers
        overview.setMostActiveUsers(getMostActiveUsers(thirtyDaysAgo, today));
        overview.setTopRatedUsers(getTopRatedUsers(thirtyDaysAgo, today));
        
        return overview;
    }
    
    @Transactional(readOnly = true)
    public UserInsightsDTO getUserInsights(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        UserInsightsDTO insights = new UserInsightsDTO();
        
        // Basic user info
        insights.setUserId(userId);
        insights.setUserName(user.getName());
        insights.setMemberSince(user.getCreatedAt().toLocalDate());
        
        // Get user analytics data
        List<UserAnalytics> userAnalytics = userAnalyticsRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, thirtyDaysAgo, today);
        
        // Calculate aggregated metrics
        calculateActivitySummary(insights, userAnalytics);
        calculateMatchingPerformance(insights, userId, thirtyDaysAgo, today);
        calculateCommunicationStats(insights, userAnalytics);
        calculateMeetingPerformance(insights, userId, thirtyDaysAgo, today);
        calculateCommunityEngagement(insights, userAnalytics);
        calculateFeedbackRatings(insights, userId, thirtyDaysAgo, today);
        calculateStreaksAndAchievements(insights, userId);
        
        // Generate trends
        insights.setActivityTrend(createActivityTrend(userAnalytics));
        insights.setWeeklyStats(createWeeklyStats(userAnalytics));
        
        // Generate insights and recommendations
        insights.setInsights(generateUserInsights(insights));
        insights.setRecommendations(generateUserRecommendations(insights));
        insights.setEngagementLevel(calculateEngagementLevel(insights));
        
        return insights;
    }
    
    @Scheduled(cron = "0 0 1 * * ?") // Run daily at 1 AM
    @Transactional
    public void generateDailyAnalytics() {
        log.info("Starting daily analytics generation...");
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // Generate platform analytics
        generatePlatformAnalytics(yesterday);
        
        // Generate user analytics
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        for (User user : activeUsers) {
            generateUserAnalytics(user.getId(), yesterday);
        }
        
        log.info("Daily analytics generation completed");
    }
    
    private void generatePlatformAnalytics(LocalDate date) {
        PlatformAnalytics analytics = new PlatformAnalytics();
        analytics.setDate(date);
        
        // Calculate user metrics
        analytics.setTotalUsers(userRepository.count());
        analytics.setNewUsersToday(userRepository.countByCreatedAtBetween(
            date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
        analytics.setActiveUsersToday(userAnalyticsRepository.getActiveUsersForDate(date));
        
        // Calculate match metrics
        analytics.setTotalMatches(matchRepository.count());
        analytics.setMatchesCreatedToday(matchRepository.countByCreatedAtBetween(
            date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
        
        // Calculate meeting metrics
        analytics.setMeetingsScheduledToday(scheduledMeetingRepository.countByCreatedAtBetween(
            date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
        
        // Calculate lounge metrics - TODO: implement when LoungeRepository is available
        analytics.setTotalLounges(0L);
        long messagesSent = 0;
        
        // Calculate rates and averages
        calculatePlatformRates(analytics, date);
        
        platformAnalyticsRepository.save(analytics);
    }
    
    private void generateUserAnalytics(Long userId, LocalDate date) {
        UserAnalytics analytics = new UserAnalytics();
        analytics.setUserId(userId);
        analytics.setDate(date);
        
        // Calculate user-specific metrics for the date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        // Get user interactions for the day
        List<UserInteraction> interactions = userInteractionRepository.findByUserIdAndCreatedAtBetween(
            userId, startOfDay, endOfDay);
        
        // Calculate metrics based on interactions
        calculateUserMetricsFromInteractions(analytics, interactions);
        
        analytics.setMessagesSent(0);
        
        userAnalyticsRepository.save(analytics);
    }
    
    private void calculateActivitySummary(UserInsightsDTO insights, List<UserAnalytics> userAnalytics) {
        insights.setTotalLogins(userAnalytics.stream().mapToInt(UserAnalytics::getLoginCount).sum());
        insights.setTotalSessionMinutes(userAnalytics.stream().mapToInt(UserAnalytics::getSessionDurationMinutes).sum());
        insights.setTotalActionsPerformed(userAnalytics.stream().mapToInt(UserAnalytics::getActionsPerformed).sum());
        
        if (!userAnalytics.isEmpty()) {
            insights.setAverageSessionDuration(insights.getTotalSessionMinutes() / userAnalytics.size());
        }
    }
    
    private void calculateMatchingPerformance(UserInsightsDTO insights, Long userId, LocalDate startDate, LocalDate endDate) {
        // Get match statistics from the database
        Long matchesInitiated = matchRepository.countByUser1IdAndCreatedAtBetween(
            userId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        Long matchesReceived = matchRepository.countByUser2IdAndCreatedAtBetween(
            userId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        
        insights.setTotalMatchesInitiated(matchesInitiated.intValue());
        insights.setTotalMatchesReceived(matchesReceived.intValue());
        
        // Calculate success rates
        if (insights.getTotalMatchesInitiated() > 0) {
            insights.setMatchSuccessRate(
                (double) insights.getTotalMatchesCompleted() / insights.getTotalMatchesInitiated() * 100);
        }
    }
    
    private void calculateCommunicationStats(UserInsightsDTO insights, List<UserAnalytics> userAnalytics) {
        insights.setTotalMessagesSent(userAnalytics.stream().mapToInt(UserAnalytics::getMessagesSent).sum());
        insights.setTotalMessagesReceived(userAnalytics.stream().mapToInt(UserAnalytics::getMessagesReceived).sum());
        insights.setConversationsStarted(userAnalytics.stream().mapToInt(UserAnalytics::getConversationsStarted).sum());
    }
    
    private void calculateMeetingPerformance(UserInsightsDTO insights, Long userId, LocalDate startDate, LocalDate endDate) {
        // Implementation for meeting performance calculation
        insights.setTotalMeetingsScheduled(0);
        insights.setTotalMeetingsAttended(0);
        insights.setTotalMeetingMinutes(0);
        insights.setMeetingAttendanceRate(0.0);
    }
    
    private void calculateCommunityEngagement(UserInsightsDTO insights, List<UserAnalytics> userAnalytics) {
        insights.setLoungesJoined(userAnalytics.stream().mapToInt(UserAnalytics::getLoungesJoined).sum());
        insights.setLoungeMessagesPosted(userAnalytics.stream().mapToInt(UserAnalytics::getLoungeMessagesSent).sum());
        insights.setLoungesCreated(userAnalytics.stream().mapToInt(UserAnalytics::getLoungesCreated).sum());
        
        // Calculate engagement score
        double engagementScore = (insights.getLoungesJoined() * 1.0 + 
                                 insights.getLoungeMessagesPosted() * 0.5 + 
                                 insights.getLoungesCreated() * 2.0) / 10.0;
        insights.setCommunityEngagementScore(Math.min(engagementScore, 10.0));
    }
    
    private void calculateFeedbackRatings(UserInsightsDTO insights, Long userId, LocalDate startDate, LocalDate endDate) {
        // Get feedback statistics
        List<MatchFeedback> feedbackGiven = matchFeedbackRepository.findByUserIdAndCreatedAtBetween(
            userId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        
        insights.setFeedbackGiven(feedbackGiven.size());
        
        if (!feedbackGiven.isEmpty()) {
            double avgRating = feedbackGiven.stream()
                .mapToInt(MatchFeedback::getQualityRating)
                .average()
                .orElse(0.0);
            insights.setAverageRatingGiven(avgRating);
        }
    }
    
    private void calculateStreaksAndAchievements(UserInsightsDTO insights, Long userId) {
        // TODO: Implement streak calculation when UserStreakRepository is available
        // Set default values for now
        insights.setCurrentLoginStreak(0);
        insights.setLongestLoginStreak(0);
        insights.setCurrentMatchStreak(0);
    }
    
    private List<UserInsightsDTO.DailyActivityDTO> createActivityTrend(List<UserAnalytics> userAnalytics) {
        return userAnalytics.stream()
            .map(ua -> new UserInsightsDTO.DailyActivityDTO(
                ua.getDate(),
                ua.getLoginCount(),
                ua.getActionsPerformed(),
                ua.getMatchesInitiated(),
                ua.getMessagesSent() + ua.getMessagesReceived()
            ))
            .collect(Collectors.toList());
    }
    
    private List<UserInsightsDTO.WeeklyStatsDTO> createWeeklyStats(List<UserAnalytics> userAnalytics) {
        // Group by weeks and calculate weekly statistics
        return new ArrayList<>(); // Simplified for now
    }
    
    private List<String> generateUserInsights(UserInsightsDTO insights) {
        List<String> userInsights = new ArrayList<>();
        
        if (insights.getMatchSuccessRate() > 80) {
            userInsights.add("You have an excellent match success rate! Your networking skills are paying off.");
        }
        
        if (insights.getCurrentLoginStreak() > 7) {
            userInsights.add("Great consistency! You've maintained a " + insights.getCurrentLoginStreak() + "-day login streak.");
        }
        
        if (insights.getCommunityEngagementScore() > 7) {
            userInsights.add("You're a highly engaged community member with strong participation in lounges.");
        }
        
        return userInsights;
    }
    
    private List<String> generateUserRecommendations(UserInsightsDTO insights) {
        List<String> recommendations = new ArrayList<>();
        
        if (insights.getMatchSuccessRate() < 50) {
            recommendations.add("Consider updating your profile to improve match compatibility.");
        }
        
        if (insights.getLoungesJoined() < 3) {
            recommendations.add("Join more topic lounges to expand your network and interests.");
        }
        
        if (insights.getFeedbackGiven() == 0) {
            recommendations.add("Provide feedback on your matches to help improve the platform.");
        }
        
        return recommendations;
    }
    
    private String calculateEngagementLevel(UserInsightsDTO insights) {
        double score = 0;
        
        // Calculate engagement score based on various metrics
        score += Math.min(insights.getTotalLogins() / 30.0, 1.0) * 25; // Login frequency
        score += Math.min(insights.getMatchSuccessRate() / 100.0, 1.0) * 25; // Match success
        score += Math.min(insights.getCommunityEngagementScore() / 10.0, 1.0) * 25; // Community engagement
        score += Math.min(insights.getAverageRatingReceived() / 5.0, 1.0) * 25; // User rating
        
        if (score >= 80) return "VERY_HIGH";
        if (score >= 60) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }
    
    // Helper methods for trend creation
    private List<AnalyticsOverviewDTO.DailyMetricDTO> createUserGrowthTrend(List<PlatformAnalytics> trendData) {
        return trendData.stream()
            .map(pa -> new AnalyticsOverviewDTO.DailyMetricDTO(
                pa.getDate(), 
                pa.getUserGrowthRate(), 
                "User Growth"
            ))
            .collect(Collectors.toList());
    }
    
    private List<AnalyticsOverviewDTO.DailyMetricDTO> createMatchSuccessTrend(List<PlatformAnalytics> trendData) {
        return trendData.stream()
            .map(pa -> new AnalyticsOverviewDTO.DailyMetricDTO(
                pa.getDate(), 
                pa.getMatchSuccessRate(), 
                "Match Success"
            ))
            .collect(Collectors.toList());
    }
    
    private List<AnalyticsOverviewDTO.DailyMetricDTO> createEngagementTrend(List<PlatformAnalytics> trendData) {
        return trendData.stream()
            .map(pa -> new AnalyticsOverviewDTO.DailyMetricDTO(
                pa.getDate(), 
                pa.getUserInteractionsToday().doubleValue(), 
                "User Interactions"
            ))
            .collect(Collectors.toList());
    }
    
    private List<AnalyticsOverviewDTO.TopUserDTO> getMostActiveUsers(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = userAnalyticsRepository.getMostActiveUsers(startDate, endDate);
        return results.stream()
            .limit(10)
            .map(result -> {
                Long userId = (Long) result[0];
                Double totalActions = (Double) result[1];
                Optional<User> user = userRepository.findById(userId);
                return new AnalyticsOverviewDTO.TopUserDTO(
                    userId,
                    user.map(User::getName).orElse("Unknown"),
                    totalActions,
                    "Total Actions"
                );
            })
            .collect(Collectors.toList());
    }
    
    private List<AnalyticsOverviewDTO.TopUserDTO> getTopRatedUsers(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = userAnalyticsRepository.getTopEngagedUsers(startDate, endDate);
        return results.stream()
            .limit(10)
            .map(result -> {
                Long userId = (Long) result[0];
                Double avgScore = (Double) result[1];
                Optional<User> user = userRepository.findById(userId);
                return new AnalyticsOverviewDTO.TopUserDTO(
                    userId,
                    user.map(User::getName).orElse("Unknown"),
                    avgScore,
                    "Engagement Score"
                );
            })
            .collect(Collectors.toList());
    }
    
    private void calculatePlatformRates(PlatformAnalytics analytics, LocalDate date) {
        // Calculate various rates and averages
        if (analytics.getTotalMatches() > 0) {
            analytics.setMatchSuccessRate(
                (double) analytics.getMatchesCompletedToday() / analytics.getMatchesCreatedToday() * 100);
        }
        
        if (analytics.getMeetingsScheduledToday() > 0) {
            analytics.setMeetingCompletionRate(
                (double) analytics.getMeetingsCompletedToday() / analytics.getMeetingsScheduledToday() * 100);
        }
    }
    
    private void calculateUserMetricsFromInteractions(UserAnalytics analytics, List<UserInteraction> interactions) {
        int profileViews = 0;
        int matchesAccepted = 0;
        int matchesRejected = 0;
        int messagesSent = 0;
        int loungesJoined = 0;
        int feedbackGiven = 0;
        
        // Count different types of interactions
        for (UserInteraction interaction : interactions) {
            switch (interaction.getInteractionType()) {
                case PROFILE_VIEW:
                    profileViews++;
                    break;
                case MATCH_ACCEPTED:
                    matchesAccepted++;
                    break;
                case MATCH_REJECTED:
                    matchesRejected++;
                    break;
                case MESSAGE_SENT:
                    messagesSent++;
                    break;
                case LOUNGE_JOINED:
                    loungesJoined++;
                    break;
                case FEEDBACK_GIVEN:
                    feedbackGiven++;
                    break;
                case MEETING_COMPLETED:
                    // Handle meeting completion
                    break;
                case SKILL_SEARCH:
                    // Handle skill search
                    break;
                case INTEREST_SEARCH:
                    // Handle interest search
                    break;
            }
        }
        
        analytics.setProfileViews(profileViews);
        analytics.setMatchesAccepted(matchesAccepted);
        analytics.setMatchesRejected(matchesRejected);
        analytics.setMessagesSent(messagesSent);
        analytics.setLoungesJoined(loungesJoined);
        analytics.setFeedbackGiven(feedbackGiven);
        
        analytics.setActionsPerformed(interactions.size());
    }
}
