package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_starters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationStarter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "template", nullable = false, columnDefinition = "TEXT")
    private String template;
    
    @Column(name = "category", nullable = false)
    private String category;
    
    @Column(name = "tags")
    private String tags; // Comma-separated tags for matching
    
    @Column(name = "context_type")
    @Enumerated(EnumType.STRING)
    private ContextType contextType;
    
    @Column(name = "difficulty_level")
    private Integer difficultyLevel = 1; // 1-3 (easy, medium, advanced)
    
    @Column(name = "usage_count", nullable = false)
    private Long usageCount = 0L;
    
    @Column(name = "success_rate")
    private Double successRate = 0.0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ContextType {
        SKILL_BASED,
        INDUSTRY_BASED,
        INTEREST_BASED,
        GENERAL,
        ICEBREAKER,
        PROFESSIONAL,
        CASUAL
    }
}
