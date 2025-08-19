package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.UserPreferences;
import lombok.Data;

@Data
public class UserPreferencesDto {
    
    private String preferredIndustries;
    private String preferredRoles;
    private UserPreferences.ExperienceLevel preferredExperienceLevel;
    private Integer maxMatchDistanceKm;
    private Integer preferredChatDuration = 30;
    private String availabilityStartTime;
    private String availabilityEndTime;
    private String preferredTimezone;
    private Boolean isAvailableForMatching = true;
    private Boolean autoAcceptMatches = false;
    private String notificationPreferences;
}
