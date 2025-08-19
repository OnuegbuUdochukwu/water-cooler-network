package com.codewithudo.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class NotificationPreferencesDto {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    // Email notification preferences
    private Boolean emailEnabled = true;
    private Boolean emailMatchNotifications = true;
    private Boolean emailMeetingReminders = true;
    private Boolean emailBadgeNotifications = true;
    private Boolean emailLoungeInvitations = true;
    private Boolean emailSystemAnnouncements = true;
    private Boolean emailCorporateAnnouncements = true;
    
    // Push notification preferences
    private Boolean pushEnabled = true;
    private Boolean pushMatchNotifications = true;
    private Boolean pushMeetingReminders = true;
    private Boolean pushBadgeNotifications = true;
    private Boolean pushLoungeInvitations = true;
    private Boolean pushSystemAnnouncements = true;
    private Boolean pushCorporateAnnouncements = true;
    
    // In-app notification preferences
    private Boolean inAppEnabled = true;
    private Boolean inAppMatchNotifications = true;
    private Boolean inAppMeetingReminders = true;
    private Boolean inAppBadgeNotifications = true;
    private Boolean inAppLoungeInvitations = true;
    private Boolean inAppSystemAnnouncements = true;
    private Boolean inAppCorporateAnnouncements = true;
    
    // General preferences
    private Boolean quietHoursEnabled = false;
    private String quietHoursStart;
    private String quietHoursEnd;
    private String timezone = "UTC";
    private String language = "en";
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public NotificationPreferencesDto() {}
    
    public NotificationPreferencesDto(Long userId) {
        this.userId = userId;
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
    
    public Boolean getEmailEnabled() {
        return emailEnabled;
    }
    
    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }
    
    public Boolean getEmailMatchNotifications() {
        return emailMatchNotifications;
    }
    
    public void setEmailMatchNotifications(Boolean emailMatchNotifications) {
        this.emailMatchNotifications = emailMatchNotifications;
    }
    
    public Boolean getEmailMeetingReminders() {
        return emailMeetingReminders;
    }
    
    public void setEmailMeetingReminders(Boolean emailMeetingReminders) {
        this.emailMeetingReminders = emailMeetingReminders;
    }
    
    public Boolean getEmailBadgeNotifications() {
        return emailBadgeNotifications;
    }
    
    public void setEmailBadgeNotifications(Boolean emailBadgeNotifications) {
        this.emailBadgeNotifications = emailBadgeNotifications;
    }
    
    public Boolean getEmailLoungeInvitations() {
        return emailLoungeInvitations;
    }
    
    public void setEmailLoungeInvitations(Boolean emailLoungeInvitations) {
        this.emailLoungeInvitations = emailLoungeInvitations;
    }
    
    public Boolean getEmailSystemAnnouncements() {
        return emailSystemAnnouncements;
    }
    
    public void setEmailSystemAnnouncements(Boolean emailSystemAnnouncements) {
        this.emailSystemAnnouncements = emailSystemAnnouncements;
    }
    
    public Boolean getEmailCorporateAnnouncements() {
        return emailCorporateAnnouncements;
    }
    
    public void setEmailCorporateAnnouncements(Boolean emailCorporateAnnouncements) {
        this.emailCorporateAnnouncements = emailCorporateAnnouncements;
    }
    
    public Boolean getPushEnabled() {
        return pushEnabled;
    }
    
    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }
    
    public Boolean getPushMatchNotifications() {
        return pushMatchNotifications;
    }
    
    public void setPushMatchNotifications(Boolean pushMatchNotifications) {
        this.pushMatchNotifications = pushMatchNotifications;
    }
    
    public Boolean getPushMeetingReminders() {
        return pushMeetingReminders;
    }
    
    public void setPushMeetingReminders(Boolean pushMeetingReminders) {
        this.pushMeetingReminders = pushMeetingReminders;
    }
    
    public Boolean getPushBadgeNotifications() {
        return pushBadgeNotifications;
    }
    
    public void setPushBadgeNotifications(Boolean pushBadgeNotifications) {
        this.pushBadgeNotifications = pushBadgeNotifications;
    }
    
    public Boolean getPushLoungeInvitations() {
        return pushLoungeInvitations;
    }
    
    public void setPushLoungeInvitations(Boolean pushLoungeInvitations) {
        this.pushLoungeInvitations = pushLoungeInvitations;
    }
    
    public Boolean getPushSystemAnnouncements() {
        return pushSystemAnnouncements;
    }
    
    public void setPushSystemAnnouncements(Boolean pushSystemAnnouncements) {
        this.pushSystemAnnouncements = pushSystemAnnouncements;
    }
    
    public Boolean getPushCorporateAnnouncements() {
        return pushCorporateAnnouncements;
    }
    
    public void setPushCorporateAnnouncements(Boolean pushCorporateAnnouncements) {
        this.pushCorporateAnnouncements = pushCorporateAnnouncements;
    }
    
    public Boolean getInAppEnabled() {
        return inAppEnabled;
    }
    
    public void setInAppEnabled(Boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }
    
    public Boolean getInAppMatchNotifications() {
        return inAppMatchNotifications;
    }
    
    public void setInAppMatchNotifications(Boolean inAppMatchNotifications) {
        this.inAppMatchNotifications = inAppMatchNotifications;
    }
    
    public Boolean getInAppMeetingReminders() {
        return inAppMeetingReminders;
    }
    
    public void setInAppMeetingReminders(Boolean inAppMeetingReminders) {
        this.inAppMeetingReminders = inAppMeetingReminders;
    }
    
    public Boolean getInAppBadgeNotifications() {
        return inAppBadgeNotifications;
    }
    
    public void setInAppBadgeNotifications(Boolean inAppBadgeNotifications) {
        this.inAppBadgeNotifications = inAppBadgeNotifications;
    }
    
    public Boolean getInAppLoungeInvitations() {
        return inAppLoungeInvitations;
    }
    
    public void setInAppLoungeInvitations(Boolean inAppLoungeInvitations) {
        this.inAppLoungeInvitations = inAppLoungeInvitations;
    }
    
    public Boolean getInAppSystemAnnouncements() {
        return inAppSystemAnnouncements;
    }
    
    public void setInAppSystemAnnouncements(Boolean inAppSystemAnnouncements) {
        this.inAppSystemAnnouncements = inAppSystemAnnouncements;
    }
    
    public Boolean getInAppCorporateAnnouncements() {
        return inAppCorporateAnnouncements;
    }
    
    public void setInAppCorporateAnnouncements(Boolean inAppCorporateAnnouncements) {
        this.inAppCorporateAnnouncements = inAppCorporateAnnouncements;
    }
    
    public Boolean getQuietHoursEnabled() {
        return quietHoursEnabled;
    }
    
    public void setQuietHoursEnabled(Boolean quietHoursEnabled) {
        this.quietHoursEnabled = quietHoursEnabled;
    }
    
    public String getQuietHoursStart() {
        return quietHoursStart;
    }
    
    public void setQuietHoursStart(String quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }
    
    public String getQuietHoursEnd() {
        return quietHoursEnd;
    }
    
    public void setQuietHoursEnd(String quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
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
}
