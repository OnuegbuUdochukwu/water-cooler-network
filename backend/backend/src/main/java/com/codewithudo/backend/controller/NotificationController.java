package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.CreateNotificationDto;
import com.codewithudo.backend.dto.NotificationDto;
import com.codewithudo.backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody CreateNotificationDto createDto) {
        try {
            NotificationDto notification = notificationService.createNotification(createDto);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long currentUserId = getCurrentUserId();
            Page<NotificationDto> notifications = notificationService.getUserNotifications(currentUserId, page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        try {
            Long currentUserId = getCurrentUserId();
            List<NotificationDto> notifications = notificationService.getUnreadNotifications(currentUserId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount() {
        try {
            Long currentUserId = getCurrentUserId();
            Long count = notificationService.getUnreadCount(currentUserId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationDto>> getRecentNotifications() {
        try {
            Long currentUserId = getCurrentUserId();
            List<NotificationDto> notifications = notificationService.getRecentNotifications(currentUserId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/priority/high")
    public ResponseEntity<List<NotificationDto>> getHighPriorityNotifications() {
        try {
            Long currentUserId = getCurrentUserId();
            List<NotificationDto> notifications = notificationService.getHighPriorityUnreadNotifications(currentUserId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserId();
            boolean updated = notificationService.markAsRead(id, currentUserId);
            if (updated) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<Integer> markAllAsRead() {
        try {
            Long currentUserId = getCurrentUserId();
            int updated = notificationService.markAllAsRead(currentUserId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserId();
            boolean deleted = notificationService.deleteNotification(id, currentUserId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // This is a simplified approach - in production, you'd want to get the user ID from the JWT token
        // For now, we'll return a placeholder
        return 1L; // Placeholder - implement proper user ID extraction
    }
}
