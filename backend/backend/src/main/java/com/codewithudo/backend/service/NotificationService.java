package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.CreateNotificationDto;
import com.codewithudo.backend.dto.NotificationDto;
import com.codewithudo.backend.entity.Notification;
import com.codewithudo.backend.entity.Notification.NotificationType;
import com.codewithudo.backend.entity.Notification.NotificationPriority;
import com.codewithudo.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // Create notification
    public NotificationDto createNotification(CreateNotificationDto createDto) {
        Notification notification = new Notification();
        notification.setUserId(createDto.getUserId());
        notification.setTitle(createDto.getTitle());
        notification.setMessage(createDto.getMessage());
        notification.setType(createDto.getType());
        notification.setPriority(createDto.getPriority());
        notification.setActionUrl(createDto.getActionUrl());
        notification.setMetadata(createDto.getMetadata());
        notification.setExpiresAt(createDto.getExpiresAt());
        
        notification = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        NotificationDto notificationDto = convertToDto(notification);
        sendRealTimeNotification(notificationDto);
        
        return notificationDto;
    }
    
    // Send real-time notification via WebSocket
    private void sendRealTimeNotification(NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
            notification.getUserId().toString(),
            "/queue/notifications",
            notification
        );
    }
    
    // Get user notifications with pagination
    public Page<NotificationDto> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::convertToDto);
    }
    
    // Get unread notifications
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get unread count
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    // Mark notification as read
    public boolean markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId, LocalDateTime.now());
        
        if (updated > 0) {
            // Send updated count via WebSocket
            Long unreadCount = getUnreadCount(userId);
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/unread-count",
                unreadCount
            );
        }
        
        return updated > 0;
    }
    
    // Mark all notifications as read
    public int markAllAsRead(Long userId) {
        int updated = notificationRepository.markAllAsRead(userId, LocalDateTime.now());
        
        if (updated > 0) {
            // Send updated count via WebSocket
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/unread-count",
                0L
            );
        }
        
        return updated;
    }
    
    // Get recent notifications (last 24 hours)
    public List<NotificationDto> getRecentNotifications(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Notification> notifications = notificationRepository.findRecentNotifications(userId, since);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get high priority unread notifications
    public List<NotificationDto> getHighPriorityUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findHighPriorityUnreadNotifications(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Delete notification
    public boolean deleteNotification(Long notificationId, Long userId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent() && notification.get().getUserId().equals(userId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
    
    // Clean up expired notifications
    @Transactional
    public int cleanupExpiredNotifications() {
        return notificationRepository.deleteExpiredNotifications(LocalDateTime.now());
    }
    
    // Helper methods for creating specific notification types
    public NotificationDto createMatchNotification(Long userId, String matchName) {
        CreateNotificationDto dto = new CreateNotificationDto(
            userId,
            "New Match Found!",
            "You have a new match with " + matchName + ". Start a conversation!",
            NotificationType.MATCH_FOUND,
            NotificationPriority.HIGH
        );
        dto.setActionUrl("/matches");
        return createNotification(dto);
    }
    
    public NotificationDto createMessageNotification(Long userId, String senderName) {
        CreateNotificationDto dto = new CreateNotificationDto(
            userId,
            "New Message",
            "You received a new message from " + senderName,
            NotificationType.MESSAGE_RECEIVED,
            NotificationPriority.MEDIUM
        );
        dto.setActionUrl("/messages");
        return createNotification(dto);
    }
    
    public NotificationDto createMeetingNotification(Long userId, String meetingTitle, LocalDateTime meetingTime) {
        CreateNotificationDto dto = new CreateNotificationDto(
            userId,
            "Meeting Scheduled",
            "Your meeting '" + meetingTitle + "' is scheduled for " + meetingTime.toString(),
            NotificationType.MEETING_SCHEDULED,
            NotificationPriority.HIGH
        );
        dto.setActionUrl("/meetings");
        return createNotification(dto);
    }
    
    public NotificationDto createBadgeNotification(Long userId, String badgeName) {
        CreateNotificationDto dto = new CreateNotificationDto(
            userId,
            "Badge Earned!",
            "Congratulations! You've earned the '" + badgeName + "' badge",
            NotificationType.BADGE_EARNED,
            NotificationPriority.MEDIUM
        );
        dto.setActionUrl("/profile");
        return createNotification(dto);
    }
    
    // Convert entity to DTO
    private NotificationDto convertToDto(Notification notification) {
        return new NotificationDto(
            notification.getId(),
            notification.getUserId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType(),
            notification.getPriority(),
            notification.getIsRead(),
            notification.getActionUrl(),
            notification.getMetadata(),
            notification.getCreatedAt(),
            notification.getReadAt(),
            notification.getExpiresAt()
        );
    }
}
