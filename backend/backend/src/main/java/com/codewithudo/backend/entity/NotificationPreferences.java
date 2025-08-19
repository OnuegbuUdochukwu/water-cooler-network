package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    // Email notification preferences
    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = true;
    
    @Column(name = "email_match_notifications", nullable = false)
    private Boolean emailMatchNotifications = true;
    
    @Column(name = "email_meeting_reminders", nullable = false)
    private Boolean emailMeetingReminders = true;
    
    @Column(name = "email_badge_notifications", nullable = false)
    private Boolean emailBadgeNotifications = true;
    
    @Column(name = "email_lounge_invitations", nullable = false)
    private Boolean emailLoungeInvitations = true;
    
    @Column(name = "email_system_announcements", nullable = false)
    private Boolean emailSystemAnnouncements = true;
    
    @Column(name = "email_corporate_announcements", nullable = false)
    private Boolean emailCorporateAnnouncements = true;
    
    // Push notification preferences
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled = true;
    
    @Column(name = "push_match_notifications", nullable = false)
    private Boolean pushMatchNotifications = true;
    
    @Column(name = "push_meeting_reminders", nullable = false)
    private Boolean pushMeetingReminders = true;
    
    @Column(name = "push_badge_notifications", nullable = false)
    private Boolean pushBadgeNotifications = true;
    
    @Column(name = "push_lounge_invitations", nullable = false)
    private Boolean pushLoungeInvitations = true;
    
    @Column(name = "push_system_announcements", nullable = false)
    private Boolean pushSystemAnnouncements = true;
    
    @Column(name = "push_corporate_announcements", nullable = false)
    private Boolean pushCorporateAnnouncements = true;
    
    // In-app notification preferences
    @Column(name = "in_app_enabled", nullable = false)
    private Boolean inAppEnabled = true;
    
    @Column(name = "in_app_match_notifications", nullable = false)
    private Boolean inAppMatchNotifications = true;
    
    @Column(name = "in_app_meeting_reminders", nullable = false)
    private Boolean inAppMeetingReminders = true;
    
    @Column(name = "in_app_badge_notifications", nullable = false)
    private Boolean inAppBadgeNotifications = true;
    
    @Column(name = "in_app_lounge_invitations", nullable = false)
    private Boolean inAppLoungeInvitations = true;
    
    @Column(name = "in_app_system_announcements", nullable = false)
    private Boolean inAppSystemAnnouncements = true;
    
    @Column(name = "in_app_corporate_announcements", nullable = false)
    private Boolean inAppCorporateAnnouncements = true;
    
    // General preferences
    @Column(name = "quiet_hours_enabled", nullable = false)
    private Boolean quietHoursEnabled = false;
    
    @Column(name = "quiet_hours_start")
    private String quietHoursStart; // Format: "HH:mm"
    
    @Column(name = "quiet_hours_end")
    private String quietHoursEnd; // Format: "HH:mm"
    
    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";
    
    @Column(name = "language", length = 10)
    private String language = "en";
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public NotificationPreferences() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public NotificationPreferences(Long userId) {
        this();
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
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if notifications are enabled for a specific type and channel
     */
    public boolean isNotificationEnabled(Notification.NotificationType type, String channel) {
        if (channel.equals("email") && !emailEnabled) {
            return false;
        }
        if (channel.equals("push") && !pushEnabled) {
            return false;
        }
        if (channel.equals("in_app") && !inAppEnabled) {
            return false;
        }
        
        return switch (type) {
            case MATCH_FOUND -> getChannelPreference(channel, "Match");
            case MEETING_REMINDER -> getChannelPreference(channel, "Meeting");
            case BADGE_EARNED -> getChannelPreference(channel, "Badge");
            case LOUNGE_INVITATION -> getChannelPreference(channel, "Lounge");
            case SYSTEM_ANNOUNCEMENT -> getChannelPreference(channel, "System");
            case COMPANY_ANNOUNCEMENT -> getChannelPreference(channel, "Corporate");
            default -> true;
        };
    }
    
    private boolean getChannelPreference(String channel, String type) {
        return switch (channel + type) {
            case "emailMatch" -> emailMatchNotifications;
            case "emailMeeting" -> emailMeetingReminders;
            case "emailBadge" -> emailBadgeNotifications;
            case "emailLounge" -> emailLoungeInvitations;
            case "emailSystem" -> emailSystemAnnouncements;
            case "emailCorporate" -> emailCorporateAnnouncements;
            case "pushMatch" -> pushMatchNotifications;
            case "pushMeeting" -> pushMeetingReminders;
            case "pushBadge" -> pushBadgeNotifications;
            case "pushLounge" -> pushLoungeInvitations;
            case "pushSystem" -> pushSystemAnnouncements;
            case "pushCorporate" -> pushCorporateAnnouncements;
            case "in_appMatch" -> inAppMatchNotifications;
            case "in_appMeeting" -> inAppMeetingReminders;
            case "in_appBadge" -> inAppBadgeNotifications;
            case "in_appLounge" -> inAppLoungeInvitations;
            case "in_appSystem" -> inAppSystemAnnouncements;
            case "in_appCorporate" -> inAppCorporateAnnouncements;
            default -> true;
        };
    }
}
