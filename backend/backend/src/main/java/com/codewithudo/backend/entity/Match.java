package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;
    
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false)
    private MatchType matchType = MatchType.COFFEE_CHAT;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.PENDING;
    
    @Column(name = "match_time")
    private LocalDateTime matchTime;
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 30;
    
    @Column(name = "compatibility_score")
    private Double compatibilityScore;
    
    @Column(name = "match_reason")
    private String matchReason;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum MatchType {
        COFFEE_CHAT, MENTORSHIP, NETWORKING, TOPIC_DISCUSSION
    }
    
    public enum MatchStatus {
        PENDING, ACCEPTED, REJECTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
