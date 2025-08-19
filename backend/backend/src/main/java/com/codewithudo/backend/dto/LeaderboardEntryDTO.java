package com.codewithudo.backend.dto;

public class LeaderboardEntryDTO {
    
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer totalPoints;
    private Integer totalBadges;
    private Integer longestStreak;
    private String longestStreakType;
    private Integer rank;
    
    // Constructors
    public LeaderboardEntryDTO() {}
    
    public LeaderboardEntryDTO(Long userId, String userName, String userEmail, 
                              Integer totalPoints, Integer totalBadges, 
                              Integer longestStreak, String longestStreakType, Integer rank) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.totalPoints = totalPoints;
        this.totalBadges = totalBadges;
        this.longestStreak = longestStreak;
        this.longestStreakType = longestStreakType;
        this.rank = rank;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public Integer getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public Integer getTotalBadges() {
        return totalBadges;
    }
    
    public void setTotalBadges(Integer totalBadges) {
        this.totalBadges = totalBadges;
    }
    
    public Integer getLongestStreak() {
        return longestStreak;
    }
    
    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }
    
    public String getLongestStreakType() {
        return longestStreakType;
    }
    
    public void setLongestStreakType(String longestStreakType) {
        this.longestStreakType = longestStreakType;
    }
    
    public Integer getRank() {
        return rank;
    }
    
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    
    /**
     * Get rank emoji based on position
     */
    public String getRankEmoji() {
        return switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> "🏅";
        };
    }
    
    /**
     * Get rank class for styling
     */
    public String getRankClass() {
        return switch (rank) {
            case 1 -> "gold";
            case 2 -> "silver";
            case 3 -> "bronze";
            default -> "standard";
        };
    }
}
