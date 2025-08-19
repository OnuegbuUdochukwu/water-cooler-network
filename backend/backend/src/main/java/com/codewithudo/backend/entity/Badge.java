package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Badge name is required")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType badgeType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_category", nullable = false)
    private BadgeCategory badgeCategory;
    
    @Column(name = "criteria_json", columnDefinition = "TEXT")
    private String criteriaJson;
    
    @Column(name = "required_count")
    private Integer requiredCount;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "rarity_level", nullable = false)
    private Integer rarityLevel = 1; // 1=Common, 2=Rare, 3=Epic, 4=Legendary
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum BadgeType {
        STREAK,
        MILESTONE,
        SOCIAL,
        ACHIEVEMENT,
        SPECIAL
    }
    
    public enum BadgeCategory {
        LOGIN,
        COFFEE_CHAT,
        LOUNGE,
        NETWORKING,
        ENGAGEMENT,
        LEADERSHIP
    }
}
