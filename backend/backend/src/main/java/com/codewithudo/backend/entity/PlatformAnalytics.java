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
@Table(name = "platform_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;
    
    // User Metrics
    @Column(name = "total_users", nullable = false)
    private Long totalUsers = 0L;
    
    @Column(name = "new_users_today", nullable = false)
    private Long newUsersToday = 0L;
    
    @Column(name = "active_users_today", nullable = false)
    private Long activeUsersToday = 0L;
    
    @Column(name = "active_users_week", nullable = false)
    private Long activeUsersWeek = 0L;
    
    @Column(name = "active_users_month", nullable = false)
    private Long activeUsersMonth = 0L;
    
    // Match Metrics
    @Column(name = "total_matches", nullable = false)
    private Long totalMatches = 0L;
    
    @Column(name = "matches_created_today", nullable = false)
    private Long matchesCreatedToday = 0L;
    
    @Column(name = "matches_accepted_today", nullable = false)
    private Long matchesAcceptedToday = 0L;
    
    @Column(name = "matches_completed_today", nullable = false)
    private Long matchesCompletedToday = 0L;
    
    @Column(name = "match_success_rate", nullable = false)
    private Double matchSuccessRate = 0.0;
    
    // Meeting Metrics
    @Column(name = "meetings_scheduled_today", nullable = false)
    private Long meetingsScheduledToday = 0L;
    
    @Column(name = "meetings_completed_today", nullable = false)
    private Long meetingsCompletedToday = 0L;
    
    @Column(name = "average_meeting_duration", nullable = false)
    private Double averageMeetingDuration = 0.0;
    
    @Column(name = "meeting_completion_rate", nullable = false)
    private Double meetingCompletionRate = 0.0;
    
    // Lounge Metrics
    @Column(name = "total_lounges", nullable = false)
    private Long totalLounges = 0L;
    
    @Column(name = "active_lounges_today", nullable = false)
    private Long activeLoungestoday = 0L;
    
    @Column(name = "messages_sent_today", nullable = false)
    private Long messagesSentToday = 0L;
    
    @Column(name = "lounge_participants_today", nullable = false)
    private Long loungeParticipantsToday = 0L;
    
    // Engagement Metrics
    @Column(name = "average_session_duration", nullable = false)
    private Double averageSessionDuration = 0.0;
    
    @Column(name = "user_interactions_today", nullable = false)
    private Long userInteractionsToday = 0L;
    
    @Column(name = "feedback_submissions_today", nullable = false)
    private Long feedbackSubmissionsToday = 0L;
    
    @Column(name = "average_feedback_rating", nullable = false)
    private Double averageFeedbackRating = 0.0;
    
    // Growth Metrics
    @Column(name = "user_growth_rate", nullable = false)
    private Double userGrowthRate = 0.0;
    
    @Column(name = "retention_rate_7_day", nullable = false)
    private Double retentionRate7Day = 0.0;
    
    @Column(name = "retention_rate_30_day", nullable = false)
    private Double retentionRate30Day = 0.0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
