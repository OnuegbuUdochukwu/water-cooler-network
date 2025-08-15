package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "preferred_industries")
    private String preferredIndustries; // Comma-separated
    
    @Column(name = "preferred_roles")
    private String preferredRoles; // Comma-separated
    
    @Column(name = "preferred_experience_level")
    @Enumerated(EnumType.STRING)
    private ExperienceLevel preferredExperienceLevel;
    
    @Column(name = "max_match_distance_km")
    private Integer maxMatchDistanceKm;
    
    @Column(name = "preferred_chat_duration")
    private Integer preferredChatDuration = 30; // minutes
    
    @Column(name = "availability_start_time")
    private String availabilityStartTime; // HH:mm format
    
    @Column(name = "availability_end_time")
    private String availabilityEndTime; // HH:mm format
    
    @Column(name = "preferred_timezone")
    private String preferredTimezone;
    
    @Column(name = "is_available_for_matching", nullable = false)
    private Boolean isAvailableForMatching = true;
    
    @Column(name = "auto_accept_matches", nullable = false)
    private Boolean autoAcceptMatches = false;
    
    @Column(name = "notification_preferences")
    private String notificationPreferences; // JSON string
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ExperienceLevel {
        JUNIOR, MID_LEVEL, SENIOR, EXECUTIVE
    }
}
