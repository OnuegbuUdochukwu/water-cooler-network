package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    // Activity Metrics
    @Column(name = "login_count", nullable = false)
    private Integer loginCount = 0;
    
    @Column(name = "session_duration_minutes", nullable = false)
    private Integer sessionDurationMinutes = 0;
    
    @Column(name = "pages_visited", nullable = false)
    private Integer pagesVisited = 0;
    
    @Column(name = "actions_performed", nullable = false)
    private Integer actionsPerformed = 0;
    
    // Matching Metrics
    @Column(name = "matches_initiated", nullable = false)
    private Integer matchesInitiated = 0;
    
    @Column(name = "matches_received", nullable = false)
    private Integer matchesReceived = 0;
    
    @Column(name = "matches_accepted", nullable = false)
    private Integer matchesAccepted = 0;
    
    @Column(name = "matches_rejected", nullable = false)
    private Integer matchesRejected = 0;
    
    @Column(name = "matches_completed", nullable = false)
    private Integer matchesCompleted = 0;
    
    // Communication Metrics
    @Column(name = "messages_sent", nullable = false)
    private Integer messagesSent = 0;
    
    @Column(name = "messages_received", nullable = false)
    private Integer messagesReceived = 0;
    
    @Column(name = "conversations_started", nullable = false)
    private Integer conversationsStarted = 0;
    
    // Meeting Metrics
    @Column(name = "meetings_scheduled", nullable = false)
    private Integer meetingsScheduled = 0;
    
    @Column(name = "meetings_attended", nullable = false)
    private Integer meetingsAttended = 0;
    
    @Column(name = "meetings_completed", nullable = false)
    private Integer meetingsCompleted = 0;
    
    @Column(name = "total_meeting_duration_minutes", nullable = false)
    private Integer totalMeetingDurationMinutes = 0;
    
    // Lounge Metrics
    @Column(name = "lounges_joined", nullable = false)
    private Integer loungesJoined = 0;
    
    @Column(name = "lounge_messages_sent", nullable = false)
    private Integer loungeMessagesSent = 0;
    
    @Column(name = "lounges_created", nullable = false)
    private Integer loungesCreated = 0;
    
    // Feedback Metrics
    @Column(name = "feedback_given", nullable = false)
    private Integer feedbackGiven = 0;
    
    @Column(name = "average_rating_given", nullable = false)
    private Double averageRatingGiven = 0.0;
    
    @Column(name = "feedback_received", nullable = false)
    private Integer feedbackReceived = 0;
    
    @Column(name = "average_rating_received", nullable = false)
    private Double averageRatingReceived = 0.0;
    
    // Engagement Metrics
    @Column(name = "profile_views", nullable = false)
    private Integer profileViews = 0;
    
    @Column(name = "profile_viewed_by_others", nullable = false)
    private Integer profileViewedByOthers = 0;
    
    @Column(name = "search_queries", nullable = false)
    private Integer searchQueries = 0;
    
    @Column(name = "feature_usage_score", nullable = false)
    private Double featureUsageScore = 0.0;
    
    // Streak Metrics
    @Column(name = "current_login_streak", nullable = false)
    private Integer currentLoginStreak = 0;
    
    @Column(name = "current_match_streak", nullable = false)
    private Integer currentMatchStreak = 0;
    
    @Column(name = "current_lounge_streak", nullable = false)
    private Integer currentLoungeStreak = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Unique constraint on user_id and date
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
    })
    public static class UserAnalyticsConstraint {}
}
