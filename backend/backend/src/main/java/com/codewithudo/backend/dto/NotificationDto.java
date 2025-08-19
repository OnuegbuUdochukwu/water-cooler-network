package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Notification.NotificationType;
import com.codewithudo.backend.entity.Notification.NotificationPriority;
import java.time.LocalDateTime;

public class NotificationDto {
    
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private Boolean isRead;
    private String actionUrl;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime expiresAt;
    
    // Constructors
    public NotificationDto() {}
    
    public NotificationDto(Long id, Long userId, String title, String message, 
                          NotificationType type, NotificationPriority priority, 
                          Boolean isRead, String actionUrl, String metadata,
                          LocalDateTime createdAt, LocalDateTime readAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
        this.isRead = isRead;
        this.actionUrl = actionUrl;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.expiresAt = expiresAt;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationPriority getPriority() {
        return priority;
    }
    
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
