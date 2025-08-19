package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_insights")
public class UserInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "insight_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InsightType insightType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "priority_level")
    private Integer priorityLevel;

    @Column(name = "category")
    private String category;

    @Column(name = "tags")
    private String tags;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "is_actioned", nullable = false)
    private Boolean isActioned;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @Column(name = "feedback_comment", columnDefinition = "TEXT")
    private String feedbackComment;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum InsightType {
        MATCHING_IMPROVEMENT,
        SKILL_DEVELOPMENT,
        NETWORKING_OPPORTUNITY,
        CONTENT_RECOMMENDATION,
        GOAL_ACHIEVEMENT,
        BEHAVIOR_PATTERN,
        ENGAGEMENT_OPTIMIZATION,
        CAREER_GROWTH,
        MENTORSHIP_SUGGESTION,
        ACTIVITY_RECOMMENDATION
    }

    // Constructors
    public UserInsight() {}

    public UserInsight(Long userId, InsightType insightType, String title, String description, String recommendation) {
        this.userId = userId;
        this.insightType = insightType;
        this.title = title;
        this.description = description;
        this.recommendation = recommendation;
        this.isRead = false;
        this.isActioned = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public InsightType getInsightType() {
        return insightType;
    }

    public void setInsightType(InsightType insightType) {
        this.insightType = insightType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsActioned() {
        return isActioned;
    }

    public void setIsActioned(Boolean isActioned) {
        this.isActioned = isActioned;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public String getInsightTypeDisplay() {
        return insightType.name().replace("_", " ").toLowerCase();
    }

    public Boolean getIsExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public String getPriorityDisplay() {
        if (priorityLevel == null) return "Normal";
        return switch (priorityLevel) {
            case 1 -> "Low";
            case 2 -> "Normal";
            case 3 -> "High";
            case 4 -> "Critical";
            default -> "Normal";
        };
    }
}
