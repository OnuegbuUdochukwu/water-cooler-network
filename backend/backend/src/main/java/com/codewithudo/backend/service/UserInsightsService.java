package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.UserInsightDTO;
import com.codewithudo.backend.entity.UserBehavior;
import com.codewithudo.backend.entity.UserInsight;
import com.codewithudo.backend.repository.UserBehaviorRepository;
import com.codewithudo.backend.repository.UserInsightRepository;
import com.codewithudo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserInsightsService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private UserInsightRepository userInsightRepository;

    @Autowired
    private UserRepository userRepository;

    // Behavior Tracking
    public void trackBehavior(Long userId, UserBehavior.BehaviorType behaviorType, String context) {
        UserBehavior behavior = new UserBehavior(userId, behaviorType, context);
        userBehaviorRepository.save(behavior);
    }

    public void trackBehavior(Long userId, UserBehavior.BehaviorType behaviorType, 
                           Long targetId, String targetType, String context) {
        UserBehavior behavior = new UserBehavior(userId, behaviorType, targetId, targetType, context);
        userBehaviorRepository.save(behavior);
    }

    public void trackBehavior(Long userId, UserBehavior.BehaviorType behaviorType, 
                           Long targetId, String targetType, String context, 
                           Integer durationSeconds, Double intensityScore) {
        UserBehavior behavior = new UserBehavior(userId, behaviorType, targetId, targetType, context);
        behavior.setDurationSeconds(durationSeconds);
        behavior.setIntensityScore(intensityScore);
        userBehaviorRepository.save(behavior);
    }

    // Insights Generation
    public List<UserInsightDTO> generateUserInsights(Long userId) {
        List<UserInsight> insights = new ArrayList<>();
        
        // Analyze user behavior patterns
        List<UserBehavior> recentBehaviors = userBehaviorRepository.findByUserIdOrderByTimestampDesc(userId);
        
        // Generate insights based on behavior patterns
        insights.addAll(generateMatchingInsights(userId, recentBehaviors));
        insights.addAll(generateSkillDevelopmentInsights(userId, recentBehaviors));
        insights.addAll(generateNetworkingInsights(userId, recentBehaviors));
        insights.addAll(generateEngagementInsights(userId, recentBehaviors));
        insights.addAll(generateCareerGrowthInsights(userId, recentBehaviors));
        
        // Save insights
        List<UserInsight> savedInsights = userInsightRepository.saveAll(insights);
        
        return savedInsights.stream()
                .map(UserInsightDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserInsightDTO> getUserInsights(Long userId) {
        List<UserInsight> insights = userInsightRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return insights.stream()
                .map(UserInsightDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserInsightDTO> getUnreadInsights(Long userId) {
        List<UserInsight> insights = userInsightRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return insights.stream()
                .map(UserInsightDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserInsightDTO> getInsightsByType(Long userId, UserInsight.InsightType insightType) {
        List<UserInsight> insights = userInsightRepository.findByUserIdAndInsightTypeOrderByCreatedAtDesc(userId, insightType);
        return insights.stream()
                .map(UserInsightDTO::new)
                .collect(Collectors.toList());
    }

    // Insight Management
    public UserInsightDTO markInsightAsRead(Long insightId) {
        Optional<UserInsight> insight = userInsightRepository.findById(insightId);
        if (insight.isPresent()) {
            UserInsight userInsight = insight.get();
            userInsight.setIsRead(true);
            UserInsight savedInsight = userInsightRepository.save(userInsight);
            return new UserInsightDTO(savedInsight);
        }
        return null;
    }

    public UserInsightDTO markInsightAsActioned(Long insightId) {
        Optional<UserInsight> insight = userInsightRepository.findById(insightId);
        if (insight.isPresent()) {
            UserInsight userInsight = insight.get();
            userInsight.setIsActioned(true);
            UserInsight savedInsight = userInsightRepository.save(userInsight);
            return new UserInsightDTO(savedInsight);
        }
        return null;
    }

    public UserInsightDTO addInsightFeedback(Long insightId, Integer rating, String comment) {
        Optional<UserInsight> insight = userInsightRepository.findById(insightId);
        if (insight.isPresent()) {
            UserInsight userInsight = insight.get();
            userInsight.setFeedbackRating(rating);
            userInsight.setFeedbackComment(comment);
            UserInsight savedInsight = userInsightRepository.save(userInsight);
            return new UserInsightDTO(savedInsight);
        }
        return null;
    }

    // AI Recommendation Methods
    private List<UserInsight> generateMatchingInsights(Long userId, List<UserBehavior> behaviors) {
        List<UserInsight> insights = new ArrayList<>();
        
        // Analyze matching behavior
        long matchRequests = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.MATCH_REQUEST)
                .count();
        
        long matchAccepts = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.MATCH_ACCEPT)
                .count();
        
        long matchRejects = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.MATCH_REJECT)
                .count();
        
        // Generate insights based on patterns
        if (matchRequests > 0 && matchAccepts > 0) {
            double acceptRate = (double) matchAccepts / matchRequests;
            if (acceptRate < 0.3) {
                insights.add(new UserInsight(userId, UserInsight.InsightType.MATCHING_IMPROVEMENT,
                        "Improve Your Matching Success Rate",
                        "Your match acceptance rate is " + String.format("%.1f", acceptRate * 100) + "%. Consider updating your profile or preferences.",
                        "Review and update your profile, adjust matching criteria, and be more selective with match requests."));
            }
        }
        
        return insights;
    }

    private List<UserInsight> generateSkillDevelopmentInsights(Long userId, List<UserBehavior> behaviors) {
        List<UserInsight> insights = new ArrayList<>();
        
        // Analyze skill-related behaviors
        long badgesEarned = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.BADGE_EARNED)
                .count();
        
        long mentorshipSessions = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.MENTORSHIP_SESSION)
                .count();
        
        if (badgesEarned == 0 && mentorshipSessions == 0) {
            insights.add(new UserInsight(userId, UserInsight.InsightType.SKILL_DEVELOPMENT,
                    "Start Your Skill Development Journey",
                    "You haven't earned any badges or participated in mentorship sessions yet.",
                    "Join mentorship programs, participate in skill-building activities, and set learning goals."));
        }
        
        return insights;
    }

    private List<UserInsight> generateNetworkingInsights(Long userId, List<UserBehavior> behaviors) {
        List<UserInsight> insights = new ArrayList<>();
        
        // Analyze networking behaviors
        long coffeeChats = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.COFFEE_CHAT_START)
                .count();
        
        long loungeJoins = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.LOUNGE_JOIN)
                .count();
        
        if (coffeeChats < 3) {
            insights.add(new UserInsight(userId, UserInsight.InsightType.NETWORKING_OPPORTUNITY,
                    "Expand Your Network",
                    "You've only had " + coffeeChats + " coffee chat(s). More connections can lead to better opportunities.",
                    "Request more matches, join topic lounges, and actively participate in conversations."));
        }
        
        return insights;
    }

    private List<UserInsight> generateEngagementInsights(Long userId, List<UserBehavior> behaviors) {
        List<UserInsight> insights = new ArrayList<>();
        
        // Analyze engagement patterns
        long logins = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.LOGIN)
                .count();
        
        long profileUpdates = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.PROFILE_UPDATE)
                .count();
        
        if (logins > 0 && profileUpdates == 0) {
            insights.add(new UserInsight(userId, UserInsight.InsightType.ENGAGEMENT_OPTIMIZATION,
                    "Keep Your Profile Fresh",
                    "You're active on the platform but haven't updated your profile recently.",
                    "Update your profile with recent achievements, skills, and interests to attract better matches."));
        }
        
        return insights;
    }

    private List<UserInsight> generateCareerGrowthInsights(Long userId, List<UserBehavior> behaviors) {
        List<UserInsight> insights = new ArrayList<>();
        
        // Analyze career-related behaviors
        long streaksMaintained = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.STREAK_MAINTAINED)
                .count();
        
        long feedbackSubmitted = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.FEEDBACK_SUBMIT)
                .count();
        
        if (streaksMaintained > 0) {
            insights.add(new UserInsight(userId, UserInsight.InsightType.CAREER_GROWTH,
                    "Maintain Your Momentum",
                    "Great job maintaining your activity streak! Consistency is key to career growth.",
                    "Keep up the good work, set new goals, and consider mentoring others."));
        }
        
        return insights;
    }

    // Behavioral Analysis
    public Map<String, Object> analyzeUserBehavior(Long userId) {
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserIdOrderByTimestampDesc(userId);
        
        Map<String, Object> analysis = new HashMap<>();
        
        // Activity frequency
        long totalActivities = behaviors.size();
        analysis.put("totalActivities", totalActivities);
        
        // Recent activity (last 7 days)
        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        long recentActivities = behaviors.stream()
                .filter(b -> b.getTimestamp().isAfter(weekAgo))
                .count();
        analysis.put("recentActivities", recentActivities);
        
        // Behavior type distribution
        Map<String, Long> behaviorDistribution = behaviors.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getBehaviorType().name(),
                        Collectors.counting()
                ));
        analysis.put("behaviorDistribution", behaviorDistribution);
        
        // Engagement score
        double engagementScore = calculateEngagementScore(behaviors);
        analysis.put("engagementScore", engagementScore);
        
        // Recommendations
        List<String> recommendations = generateRecommendations(behaviors);
        analysis.put("recommendations", recommendations);
        
        return analysis;
    }

    private double calculateEngagementScore(List<UserBehavior> behaviors) {
        if (behaviors.isEmpty()) return 0.0;
        
        double score = 0.0;
        for (UserBehavior behavior : behaviors) {
            switch (behavior.getBehaviorType()) {
                case COFFEE_CHAT_START:
                case MENTORSHIP_SESSION:
                case LOUNGE_JOIN:
                    score += 10.0;
                    break;
                case MATCH_REQUEST:
                case BADGE_EARNED:
                    score += 5.0;
                    break;
                case PROFILE_UPDATE:
                case FEEDBACK_SUBMIT:
                    score += 3.0;
                    break;
                case LOGIN:
                    score += 1.0;
                    break;
                default:
                    score += 0.5;
            }
        }
        
        return Math.min(100.0, score);
    }

    private List<String> generateRecommendations(List<UserBehavior> behaviors) {
        List<String> recommendations = new ArrayList<>();
        
        long coffeeChats = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.COFFEE_CHAT_START)
                .count();
        
        long mentorshipSessions = behaviors.stream()
                .filter(b -> b.getBehaviorType() == UserBehavior.BehaviorType.MENTORSHIP_SESSION)
                .count();
        
        if (coffeeChats < 5) {
            recommendations.add("Try to have more coffee chats to expand your network");
        }
        
        if (mentorshipSessions == 0) {
            recommendations.add("Consider joining a mentorship program to accelerate your growth");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("You're doing great! Keep up the excellent work");
        }
        
        return recommendations;
    }
}
