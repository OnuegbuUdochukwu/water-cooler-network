package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preference_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    // ML-generated preference vectors
    @Column(name = "skill_vector", columnDefinition = "TEXT")
    private String skillVector; // JSON array of skill preferences
    
    @Column(name = "interest_vector", columnDefinition = "TEXT")
    private String interestVector; // JSON array of interest preferences
    
    @Column(name = "industry_vector", columnDefinition = "TEXT")
    private String industryVector; // JSON array of industry preferences
    
    @Column(name = "communication_style")
    @Enumerated(EnumType.STRING)
    private CommunicationStyle communicationStyle;
    
    @Column(name = "meeting_preference")
    @Enumerated(EnumType.STRING)
    private MeetingPreference meetingPreference;
    
    @Column(name = "experience_level")
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    
    @Column(name = "personality_traits", columnDefinition = "TEXT")
    private String personalityTraits; // JSON object with personality scores
    
    @Column(name = "preferred_topics", columnDefinition = "TEXT")
    private String preferredTopics; // JSON array of preferred discussion topics
    
    @Column(name = "availability_pattern", columnDefinition = "TEXT")
    private String availabilityPattern; // JSON object with time preferences
    
    @Column(name = "matching_radius")
    private Integer matchingRadius = 100; // Preference diversity radius (0-100)
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum CommunicationStyle {
        DIRECT, COLLABORATIVE, ANALYTICAL, EXPRESSIVE, SUPPORTIVE
    }
    
    public enum MeetingPreference {
        VIRTUAL, IN_PERSON, HYBRID, NO_PREFERENCE
    }
    
    public enum ExperienceLevel {
        ENTRY, MID, SENIOR, EXECUTIVE, EXPERT
    }
}
