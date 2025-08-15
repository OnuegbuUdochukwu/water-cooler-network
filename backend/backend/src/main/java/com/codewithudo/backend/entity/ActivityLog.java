package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;
    
    @Column(name = "entity_id")
    private Long entityId; // ID of related entity (match, lounge, etc.)
    
    @Column(name = "activity_data", columnDefinition = "TEXT")
    private String activityData; // JSON data for additional context
    
    @Column(name = "points_earned")
    private Integer pointsEarned = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum ActivityType {
        LOGIN,
        COFFEE_CHAT_REQUEST,
        COFFEE_CHAT_ACCEPTED,
        COFFEE_CHAT_COMPLETED,
        LOUNGE_JOINED,
        LOUNGE_CREATED,
        LOUNGE_MESSAGE_SENT,
        PROFILE_UPDATED,
        MATCH_FOUND,
        BADGE_EARNED
    }
}
