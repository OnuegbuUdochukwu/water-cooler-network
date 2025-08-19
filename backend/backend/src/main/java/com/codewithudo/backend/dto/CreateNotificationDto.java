package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Notification.NotificationType;
import com.codewithudo.backend.entity.Notification.NotificationPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateNotificationDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotNull(message = "Type is required")
    private NotificationType type;
    
    @NotNull(message = "Priority is required")
    private NotificationPriority priority;
    
    private String actionUrl;
    private String metadata;
    private LocalDateTime expiresAt;
    
    // Constructors
    public CreateNotificationDto() {}
    
    public CreateNotificationDto(Long userId, String title, String message, 
                                NotificationType type, NotificationPriority priority) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
    }
    
    // Getters and Setters
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
