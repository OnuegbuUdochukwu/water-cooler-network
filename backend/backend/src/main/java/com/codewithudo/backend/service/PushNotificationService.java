package com.codewithudo.backend.service;

import com.codewithudo.backend.entity.Notification;
import com.codewithudo.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PushNotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Value("${app.notifications.push.enabled:true}")
    private boolean pushEnabled;
    
    @Value("${app.notifications.push.vapid.public-key:}")
    private String vapidPublicKey;
    
    @Value("${app.notifications.push.vapid.private-key:}")
    private String vapidPrivateKey;
    
    // Store user push subscription tokens (in production, use database)
    private final Map<Long, String> userPushTokens = new ConcurrentHashMap<>();
    
    /**
     * Register push subscription for a user
     */
    public void registerPushSubscription(Long userId, String subscriptionToken) {
        if (pushEnabled) {
            userPushTokens.put(userId, subscriptionToken);
        }
    }
    
    /**
     * Unregister push subscription for a user
     */
    public void unregisterPushSubscription(Long userId) {
        userPushTokens.remove(userId);
    }
    
    /**
     * Send push notification to specific user
     */
    public void sendPushNotification(Long userId, Notification notification) {
        if (!pushEnabled) {
            return;
        }
        
        // Send via WebSocket for real-time delivery
        sendWebSocketNotification(userId, notification);
        
        // Send push notification if user has registered
        String pushToken = userPushTokens.get(userId);
        if (pushToken != null) {
            sendBrowserPushNotification(userId, pushToken, notification);
        }
    }
    
    /**
     * Send push notification to multiple users
     */
    public void sendBulkPushNotification(Iterable<Long> userIds, Notification notification) {
        if (!pushEnabled) {
            return;
        }
        
        for (Long userId : userIds) {
            sendPushNotification(userId, notification);
        }
    }
    
    /**
     * Send match notification push
     */
    public void sendMatchPushNotification(Long userId, String matchName, String matchReason) {
        if (!pushEnabled) {
            return;
        }
        
        Notification notification = new Notification();
        notification.setTitle("New Match Found!");
        notification.setMessage("You have a new match with " + matchName);
        notification.setType(Notification.NotificationType.MATCH_FOUND);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setActionUrl("/matches");
        
        sendPushNotification(userId, notification);
    }
    
    /**
     * Send meeting reminder push
     */
    public void sendMeetingReminderPush(Long userId, String meetingTitle, String meetingTime, String participantName) {
        if (!pushEnabled) {
            return;
        }
        
        Notification notification = new Notification();
        notification.setTitle("Meeting Reminder");
        notification.setMessage("Your meeting '" + meetingTitle + "' with " + participantName + " is at " + meetingTime);
        notification.setType(Notification.NotificationType.MEETING_REMINDER);
        notification.setPriority(Notification.NotificationPriority.URGENT);
        notification.setActionUrl("/meetings");
        
        sendPushNotification(userId, notification);
    }
    
    /**
     * Send badge earned push
     */
    public void sendBadgeEarnedPush(Long userId, String badgeName, String badgeDescription) {
        if (!pushEnabled) {
            return;
        }
        
        Notification notification = new Notification();
        notification.setTitle("Badge Earned!");
        notification.setMessage("Congratulations! You've earned the '" + badgeName + "' badge");
        notification.setType(Notification.NotificationType.BADGE_EARNED);
        notification.setPriority(Notification.NotificationPriority.MEDIUM);
        notification.setActionUrl("/profile");
        
        sendPushNotification(userId, notification);
    }
    
    /**
     * Send lounge invitation push
     */
    public void sendLoungeInvitationPush(Long userId, String loungeName, String inviterName) {
        if (!pushEnabled) {
            return;
        }
        
        Notification notification = new Notification();
        notification.setTitle("Lounge Invitation");
        notification.setMessage(inviterName + " invited you to join '" + loungeName + "' lounge");
        notification.setType(Notification.NotificationType.LOUNGE_INVITATION);
        notification.setPriority(Notification.NotificationPriority.MEDIUM);
        notification.setActionUrl("/lounges");
        
        sendPushNotification(userId, notification);
    }
    
    /**
     * Send system announcement push
     */
    public void sendSystemAnnouncementPush(Long userId, String announcementTitle, String announcementContent) {
        if (!pushEnabled) {
            return;
        }
        
        Notification notification = new Notification();
        notification.setTitle(announcementTitle);
        notification.setMessage(announcementContent);
        notification.setType(Notification.NotificationType.SYSTEM_ANNOUNCEMENT);
        notification.setPriority(Notification.NotificationPriority.MEDIUM);
        notification.setActionUrl("/announcements");
        
        sendPushNotification(userId, notification);
    }
    
    /**
     * Send WebSocket notification for real-time delivery
     */
    private void sendWebSocketNotification(Long userId, Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
            );
        } catch (Exception e) {
            // Log error but don't fail the notification
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }
    }
    
    /**
     * Send browser push notification
     */
    private void sendBrowserPushNotification(Long userId, String pushToken, Notification notification) {
        try {
            // In a production environment, you would:
            // 1. Use a service like Firebase Cloud Messaging
            // 2. Or implement VAPID protocol for web push
            // 3. Send the notification to the browser's push service
            
            // For now, we'll just log the intent
            System.out.println("Sending push notification to user " + userId + 
                             " with token " + pushToken + 
                             " for notification: " + notification.getTitle());
            
        } catch (Exception e) {
            System.err.println("Failed to send browser push notification: " + e.getMessage());
        }
    }
    
    /**
     * Check if push notifications are enabled
     */
    public boolean isPushEnabled() {
        return pushEnabled;
    }
    
    /**
     * Get VAPID public key for frontend
     */
    public String getVapidPublicKey() {
        return vapidPublicKey;
    }
    
    /**
     * Get number of registered push subscriptions
     */
    public int getRegisteredSubscriptionCount() {
        return userPushTokens.size();
    }
}
