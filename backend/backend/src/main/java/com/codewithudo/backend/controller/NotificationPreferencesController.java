package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.NotificationPreferencesDto;
import com.codewithudo.backend.service.NotificationPreferencesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-preferences")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationPreferencesController {
    
    @Autowired
    private NotificationPreferencesService preferencesService;
    
    /**
     * Get current user's notification preferences
     */
    @GetMapping
    public ResponseEntity<NotificationPreferencesDto> getUserPreferences() {
        try {
            Long currentUserId = getCurrentUserId();
            NotificationPreferencesDto preferences = preferencesService.getUserPreferences(currentUserId);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update current user's notification preferences
     */
    @PutMapping
    public ResponseEntity<NotificationPreferencesDto> updateUserPreferences(
            @Valid @RequestBody NotificationPreferencesDto preferencesDto) {
        try {
            Long currentUserId = getCurrentUserId();
            preferencesDto.setUserId(currentUserId);
            
            NotificationPreferencesDto updatedPreferences = preferencesService.updateUserPreferences(
                currentUserId, preferencesDto);
            return ResponseEntity.ok(updatedPreferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Reset current user's notification preferences to defaults
     */
    @PostMapping("/reset")
    public ResponseEntity<NotificationPreferencesDto> resetToDefaults() {
        try {
            Long currentUserId = getCurrentUserId();
            NotificationPreferencesDto defaultPreferences = preferencesService.resetToDefaults(currentUserId);
            return ResponseEntity.ok(defaultPreferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Check if a specific notification type is enabled for current user
     */
    @GetMapping("/check/{notificationType}/{channel}")
    public ResponseEntity<Boolean> isNotificationEnabled(
            @PathVariable String notificationType,
            @PathVariable String channel) {
        try {
            Long currentUserId = getCurrentUserId();
            boolean isEnabled = preferencesService.isNotificationEnabled(currentUserId, notificationType, channel);
            return ResponseEntity.ok(isEnabled);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Check if current user is in quiet hours
     */
    @GetMapping("/quiet-hours/status")
    public ResponseEntity<Boolean> isInQuietHours() {
        try {
            Long currentUserId = getCurrentUserId();
            boolean inQuietHours = preferencesService.isInQuietHours(currentUserId);
            return ResponseEntity.ok(inQuietHours);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current user's timezone
     */
    @GetMapping("/timezone")
    public ResponseEntity<String> getUserTimezone() {
        try {
            Long currentUserId = getCurrentUserId();
            String timezone = preferencesService.getUserTimezone(currentUserId);
            return ResponseEntity.ok(timezone);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current user's language preference
     */
    @GetMapping("/language")
    public ResponseEntity<String> getUserLanguage() {
        try {
            Long currentUserId = getCurrentUserId();
            String language = preferencesService.getUserLanguage(currentUserId);
            return ResponseEntity.ok(language);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current user ID from authentication context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // This is a simplified approach - in production, you'd want to get the user ID from the JWT token
        // For now, we'll return a placeholder
        return 1L; // Placeholder - implement proper user ID extraction
    }
}
