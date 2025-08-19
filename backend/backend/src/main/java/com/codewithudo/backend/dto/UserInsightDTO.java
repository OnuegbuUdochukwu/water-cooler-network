package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.UserInsight;
import java.time.LocalDateTime;
import java.util.List;

public class UserInsightDTO {

    private Long id;
    private Long userId;
    private String insightType;
    private String title;
    private String description;
    private String recommendation;
    private Double confidenceScore;
    private Integer priorityLevel;
    private String category;
    private String tags;
    private String actionUrl;
    private Boolean isRead;
    private Boolean isActioned;
    private Integer feedbackRating;
    private String feedbackComment;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String insightTypeDisplay;
    private Boolean isExpired;
    private String priorityDisplay;

    // Constructors
    public UserInsightDTO() {}

    public UserInsightDTO(UserInsight insight) {
        this.id = insight.getId();
        this.userId = insight.getUserId();
        this.insightType = insight.getInsightType().name();
        this.title = insight.getTitle();
        this.description = insight.getDescription();
        this.recommendation = insight.getRecommendation();
        this.confidenceScore = insight.getConfidenceScore();
        this.priorityLevel = insight.getPriorityLevel();
        this.category = insight.getCategory();
        this.tags = insight.getTags();
        this.actionUrl = insight.getActionUrl();
        this.isRead = insight.getIsRead();
        this.isActioned = insight.getIsActioned();
        this.feedbackRating = insight.getFeedbackRating();
        this.feedbackComment = insight.getFeedbackComment();
        this.expiresAt = insight.getExpiresAt();
        this.createdAt = insight.getCreatedAt();
        this.updatedAt = insight.getUpdatedAt();
        this.insightTypeDisplay = insight.getInsightTypeDisplay();
        this.isExpired = insight.getIsExpired();
        this.priorityDisplay = insight.getPriorityDisplay();
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

    public String getInsightType() {
        return insightType;
    }

    public void setInsightType(String insightType) {
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

    public String getInsightTypeDisplay() {
        return insightTypeDisplay;
    }

    public void setInsightTypeDisplay(String insightTypeDisplay) {
        this.insightTypeDisplay = insightTypeDisplay;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public String getPriorityDisplay() {
        return priorityDisplay;
    }

    public void setPriorityDisplay(String priorityDisplay) {
        this.priorityDisplay = priorityDisplay;
    }

    // Helper methods
    public String getConfidenceDisplay() {
        if (confidenceScore == null) return "Unknown";
        if (confidenceScore >= 0.8) return "High";
        if (confidenceScore >= 0.6) return "Medium";
        return "Low";
    }

    public String getStatusDisplay() {
        if (isExpired) return "Expired";
        if (isActioned) return "Actioned";
        if (isRead) return "Read";
        return "New";
    }
}
