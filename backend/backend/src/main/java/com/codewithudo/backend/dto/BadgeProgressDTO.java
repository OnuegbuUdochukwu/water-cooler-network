package com.codewithudo.backend.dto;

import java.time.LocalDateTime;

public class BadgeProgressDTO {
    
    private Long badgeId;
    private String badgeName;
    private String badgeDescription;
    private String iconUrl;
    private Integer requiredCount;
    private Integer currentProgress;
    private Integer rarityLevel;
    private Boolean isEarned;
    private LocalDateTime earnedAt;
    
    // Constructors
    public BadgeProgressDTO() {}
    
    public BadgeProgressDTO(Long badgeId, String badgeName, String badgeDescription, 
                           String iconUrl, Integer requiredCount, Integer currentProgress, 
                           Integer rarityLevel, Boolean isEarned) {
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.badgeDescription = badgeDescription;
        this.iconUrl = iconUrl;
        this.requiredCount = requiredCount;
        this.currentProgress = currentProgress;
        this.rarityLevel = rarityLevel;
        this.isEarned = isEarned;
    }
    
    // Getters and Setters
    public Long getBadgeId() {
        return badgeId;
    }
    
    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
    }
    
    public String getBadgeName() {
        return badgeName;
    }
    
    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }
    
    public String getBadgeDescription() {
        return badgeDescription;
    }
    
    public void setBadgeDescription(String badgeDescription) {
        this.badgeDescription = badgeDescription;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public Integer getRequiredCount() {
        return requiredCount;
    }
    
    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }
    
    public Integer getCurrentProgress() {
        return currentProgress;
    }
    
    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }
    
    public Integer getRarityLevel() {
        return rarityLevel;
    }
    
    public void setRarityLevel(Integer rarityLevel) {
        this.rarityLevel = rarityLevel;
    }
    
    public Boolean getIsEarned() {
        return isEarned;
    }
    
    public void setIsEarned(Boolean isEarned) {
        this.isEarned = isEarned;
    }
    
    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }
    
    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
    
    /**
     * Get progress percentage
     */
    public Integer getProgressPercentage() {
        if (requiredCount == null || requiredCount == 0) {
            return 0;
        }
        return Math.min(100, (currentProgress * 100) / requiredCount);
    }
    
    /**
     * Check if badge is close to being earned
     */
    public Boolean getIsCloseToEarning() {
        if (isEarned || requiredCount == null || currentProgress == null) {
            return false;
        }
        return currentProgress >= (requiredCount * 0.8); // 80% or more
    }
    
    /**
     * Get rarity level description
     */
    public String getRarityDescription() {
        return switch (rarityLevel) {
            case 1 -> "Common";
            case 2 -> "Rare";
            case 3 -> "Epic";
            case 4 -> "Legendary";
            default -> "Unknown";
        };
    }
}
