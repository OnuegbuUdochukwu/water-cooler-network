package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "target_user_id")
    private Long targetUserId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;
    
    @Column(name = "interaction_value")
    private String interactionValue;
    
    @Column(name = "weight", nullable = false)
    private Double weight = 1.0;
    
    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum InteractionType {
        PROFILE_VIEW,
        MATCH_ACCEPTED,
        MATCH_REJECTED,
        MESSAGE_SENT,
        MEETING_COMPLETED,
        SKILL_SEARCH,
        INTEREST_SEARCH,
        LOUNGE_JOINED,
        FEEDBACK_GIVEN
    }
}
