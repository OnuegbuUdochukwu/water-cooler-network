package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "match_id", nullable = false)
    private Long matchId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "quality_rating", nullable = false)
    private Integer qualityRating; // 1-5 scale
    
    @Column(name = "conversation_rating")
    private Integer conversationRating; // 1-5 scale
    
    @Column(name = "relevance_rating")
    private Integer relevanceRating; // 1-5 scale
    
    @Column(name = "would_meet_again")
    private Boolean wouldMeetAgain;
    
    @Column(name = "feedback_text", columnDefinition = "TEXT")
    private String feedbackText;
    
    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;
    
    @Column(name = "tags")
    private String tags; // Comma-separated tags
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
