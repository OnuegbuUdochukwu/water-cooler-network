package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.NotificationPreferencesDto;
import com.codewithudo.backend.entity.NotificationPreferences;
import com.codewithudo.backend.repository.NotificationPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NotificationPreferencesService {
    
    @Autowired
    private NotificationPreferencesRepository preferencesRepository;
    
    /**
     * Get notification preferences for a user
     */
    public NotificationPreferencesDto getUserPreferences(Long userId) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        
        if (preferences.isPresent()) {
            return convertToDto(preferences.get());
        } else {
            // Create default preferences if none exist
            NotificationPreferences defaultPreferences = new NotificationPreferences(userId);
            defaultPreferences = preferencesRepository.save(defaultPreferences);
            return convertToDto(defaultPreferences);
        }
    }
    
    /**
     * Update notification preferences for a user
     */
    public NotificationPreferencesDto updateUserPreferences(Long userId, NotificationPreferencesDto preferencesDto) {
        Optional<NotificationPreferences> existingPreferences = preferencesRepository.findByUserId(userId);
        
        NotificationPreferences preferences;
        if (existingPreferences.isPresent()) {
            preferences = existingPreferences.get();
        } else {
            preferences = new NotificationPreferences(userId);
        }
        
        // Update preferences from DTO
        updatePreferencesFromDto(preferences, preferencesDto);
        preferences.setUpdatedAt(java.time.LocalDateTime.now());
        
        preferences = preferencesRepository.save(preferences);
        return convertToDto(preferences);
    }
    
    /**
     * Reset notification preferences to defaults for a user
     */
    public NotificationPreferencesDto resetToDefaults(Long userId) {
        // Delete existing preferences
        preferencesRepository.deleteByUserId(userId);
        
        // Create new default preferences
        NotificationPreferences defaultPreferences = new NotificationPreferences(userId);
        defaultPreferences = preferencesRepository.save(defaultPreferences);
        
        return convertToDto(defaultPreferences);
    }
    
    /**
     * Check if a specific notification type is enabled for a user and channel
     */
    public boolean isNotificationEnabled(Long userId, String notificationType, String channel) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        
        if (preferences.isPresent()) {
            return preferences.get().isNotificationEnabled(
                com.codewithudo.backend.entity.Notification.NotificationType.valueOf(notificationType), 
                channel
            );
        }
        
        // Default to enabled if no preferences exist
        return true;
    }
    
    /**
     * Check if quiet hours are active for a user
     */
    public boolean isInQuietHours(Long userId) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        
        if (preferences.isPresent() && preferences.get().getQuietHoursEnabled()) {
            return isCurrentTimeInQuietHours(preferences.get());
        }
        
        return false;
    }
    
    /**
     * Get user's timezone
     */
    public String getUserTimezone(Long userId) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        return preferences.map(NotificationPreferences::getTimezone).orElse("UTC");
    }
    
    /**
     * Get user's language preference
     */
    public String getUserLanguage(Long userId) {
        Optional<NotificationPreferences> preferences = preferencesRepository.findByUserId(userId);
        return preferences.map(NotificationPreferences::getLanguage).orElse("en");
    }
    
    /**
     * Check if current time is within quiet hours
     */
    private boolean isCurrentTimeInQuietHours(NotificationPreferences preferences) {
        if (!preferences.getQuietHoursEnabled() || 
            preferences.getQuietHoursStart() == null || 
            preferences.getQuietHoursEnd() == null) {
            return false;
        }
        
        try {
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.LocalTime start = java.time.LocalTime.parse(preferences.getQuietHoursStart());
            java.time.LocalTime end = java.time.LocalTime.parse(preferences.getQuietHoursEnd());
            
            if (start.isBefore(end)) {
                // Same day quiet hours (e.g., 22:00 to 08:00)
                return now.isAfter(start) && now.isBefore(end);
            } else {
                // Overnight quiet hours (e.g., 22:00 to 08:00)
                return now.isAfter(start) || now.isBefore(end);
            }
        } catch (Exception e) {
            // If parsing fails, assume not in quiet hours
            return false;
        }
    }
    
    /**
     * Convert entity to DTO
     */
    private NotificationPreferencesDto convertToDto(NotificationPreferences preferences) {
        NotificationPreferencesDto dto = new NotificationPreferencesDto();
        dto.setId(preferences.getId());
        dto.setUserId(preferences.getUserId());
        
        // Email preferences
        dto.setEmailEnabled(preferences.getEmailEnabled());
        dto.setEmailMatchNotifications(preferences.getEmailMatchNotifications());
        dto.setEmailMeetingReminders(preferences.getEmailMeetingReminders());
        dto.setEmailBadgeNotifications(preferences.getEmailBadgeNotifications());
        dto.setEmailLoungeInvitations(preferences.getEmailLoungeInvitations());
        dto.setEmailSystemAnnouncements(preferences.getEmailSystemAnnouncements());
        dto.setEmailCorporateAnnouncements(preferences.getEmailCorporateAnnouncements());
        
        // Push preferences
        dto.setPushEnabled(preferences.getPushEnabled());
        dto.setPushMatchNotifications(preferences.getPushMatchNotifications());
        dto.setPushMeetingReminders(preferences.getPushMeetingReminders());
        dto.setPushBadgeNotifications(preferences.getPushBadgeNotifications());
        dto.setPushLoungeInvitations(preferences.getPushLoungeInvitations());
        dto.setPushSystemAnnouncements(preferences.getPushSystemAnnouncements());
        dto.setPushCorporateAnnouncements(preferences.getPushCorporateAnnouncements());
        
        // In-app preferences
        dto.setInAppEnabled(preferences.getInAppEnabled());
        dto.setInAppMatchNotifications(preferences.getInAppMatchNotifications());
        dto.setInAppMeetingReminders(preferences.getInAppMeetingReminders());
        dto.setInAppBadgeNotifications(preferences.getInAppBadgeNotifications());
        dto.setInAppLoungeInvitations(preferences.getInAppLoungeInvitations());
        dto.setInAppSystemAnnouncements(preferences.getInAppSystemAnnouncements());
        dto.setInAppCorporateAnnouncements(preferences.getInAppCorporateAnnouncements());
        
        // General preferences
        dto.setQuietHoursEnabled(preferences.getQuietHoursEnabled());
        dto.setQuietHoursStart(preferences.getQuietHoursStart());
        dto.setQuietHoursEnd(preferences.getQuietHoursEnd());
        dto.setTimezone(preferences.getTimezone());
        dto.setLanguage(preferences.getLanguage());
        
        dto.setCreatedAt(preferences.getCreatedAt());
        dto.setUpdatedAt(preferences.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Update entity from DTO
     */
    private void updatePreferencesFromDto(NotificationPreferences preferences, NotificationPreferencesDto dto) {
        // Email preferences
        if (dto.getEmailEnabled() != null) preferences.setEmailEnabled(dto.getEmailEnabled());
        if (dto.getEmailMatchNotifications() != null) preferences.setEmailMatchNotifications(dto.getEmailMatchNotifications());
        if (dto.getEmailMeetingReminders() != null) preferences.setEmailMeetingReminders(dto.getEmailMeetingReminders());
        if (dto.getEmailBadgeNotifications() != null) preferences.setEmailBadgeNotifications(dto.getEmailBadgeNotifications());
        if (dto.getEmailLoungeInvitations() != null) preferences.setEmailLoungeInvitations(dto.getEmailLoungeInvitations());
        if (dto.getEmailSystemAnnouncements() != null) preferences.setEmailSystemAnnouncements(dto.getEmailSystemAnnouncements());
        if (dto.getEmailCorporateAnnouncements() != null) preferences.setEmailCorporateAnnouncements(dto.getEmailCorporateAnnouncements());
        
        // Push preferences
        if (dto.getPushEnabled() != null) preferences.setPushEnabled(dto.getPushEnabled());
        if (dto.getPushMatchNotifications() != null) preferences.setPushMatchNotifications(dto.getPushMatchNotifications());
        if (dto.getPushMeetingReminders() != null) preferences.setPushMeetingReminders(dto.getPushMeetingReminders());
        if (dto.getPushBadgeNotifications() != null) preferences.setPushBadgeNotifications(dto.getPushBadgeNotifications());
        if (dto.getPushLoungeInvitations() != null) preferences.setPushLoungeInvitations(dto.getPushLoungeInvitations());
        if (dto.getPushSystemAnnouncements() != null) preferences.setPushSystemAnnouncements(dto.getPushSystemAnnouncements());
        if (dto.getPushCorporateAnnouncements() != null) preferences.setPushCorporateAnnouncements(dto.getPushCorporateAnnouncements());
        
        // In-app preferences
        if (dto.getInAppEnabled() != null) preferences.setInAppEnabled(dto.getInAppEnabled());
        if (dto.getInAppMatchNotifications() != null) preferences.setInAppMatchNotifications(dto.getInAppMatchNotifications());
        if (dto.getInAppMeetingReminders() != null) preferences.setInAppMeetingReminders(dto.getInAppMeetingReminders());
        if (dto.getInAppBadgeNotifications() != null) preferences.setInAppBadgeNotifications(dto.getInAppBadgeNotifications());
        if (dto.getInAppLoungeInvitations() != null) preferences.setInAppLoungeInvitations(dto.getInAppLoungeInvitations());
        if (dto.getInAppSystemAnnouncements() != null) preferences.setInAppSystemAnnouncements(dto.getInAppSystemAnnouncements());
        if (dto.getInAppCorporateAnnouncements() != null) preferences.setInAppCorporateAnnouncements(dto.getInAppCorporateAnnouncements());
        
        // General preferences
        if (dto.getQuietHoursEnabled() != null) preferences.setQuietHoursEnabled(dto.getQuietHoursEnabled());
        if (dto.getQuietHoursStart() != null) preferences.setQuietHoursStart(dto.getQuietHoursStart());
        if (dto.getQuietHoursEnd() != null) preferences.setQuietHoursEnd(dto.getQuietHoursEnd());
        if (dto.getTimezone() != null) preferences.setTimezone(dto.getTimezone());
        if (dto.getLanguage() != null) preferences.setLanguage(dto.getLanguage());
    }
}
