package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_behaviors")
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "behavior_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BehaviorType behaviorType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "intensity_score")
    private Double intensityScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum BehaviorType {
        LOGIN,
        LOGOUT,
        PROFILE_VIEW,
        PROFILE_UPDATE,
        MATCH_REQUEST,
        MATCH_ACCEPT,
        MATCH_REJECT,
        COFFEE_CHAT_START,
        COFFEE_CHAT_END,
        LOUNGE_JOIN,
        LOUNGE_LEAVE,
        LOUNGE_MESSAGE,
        BADGE_EARNED,
        STREAK_MAINTAINED,
        MENTORSHIP_JOIN,
        MENTORSHIP_SESSION,
        CONTENT_VIEW,
        CONTENT_LIKE,
        CONTENT_SHARE,
        SEARCH_QUERY,
        NOTIFICATION_OPEN,
        NOTIFICATION_DISMISS,
        FEEDBACK_SUBMIT,
        RATING_GIVE,
        PREFERENCE_UPDATE
    }

    // Constructors
    public UserBehavior() {}

    public UserBehavior(Long userId, BehaviorType behaviorType, String context) {
        this.userId = userId;
        this.behaviorType = behaviorType;
        this.context = context;
        this.timestamp = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public UserBehavior(Long userId, BehaviorType behaviorType, Long targetId, String targetType, String context) {
        this.userId = userId;
        this.behaviorType = behaviorType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.context = context;
        this.timestamp = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(BehaviorType behaviorType) {
        this.behaviorType = behaviorType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Double getIntensityScore() {
        return intensityScore;
    }

    public void setIntensityScore(Double intensityScore) {
        this.intensityScore = intensityScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
