package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.*;
import com.codewithudo.backend.service.BadgeService;
import com.codewithudo.backend.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GamificationController {
    
    private final GamificationService gamificationService;
    private final BadgeService badgeService;
    
    @GetMapping("/summary")
    public ResponseEntity<GamificationSummaryDTO> getGamificationSummary(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        GamificationSummaryDTO summary = gamificationService.getUserGamificationSummary(userId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/streaks")
    public ResponseEntity<List<UserStreakDTO>> getUserStreaks(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<UserStreakDTO> streaks = gamificationService.getUserStreaks(userId);
        return ResponseEntity.ok(streaks);
    }
    
    @GetMapping("/badges")
    public ResponseEntity<List<UserBadgeDTO>> getUserBadges(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<UserBadgeDTO> badges = gamificationService.getUserBadges(userId);
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/badges/available")
    public ResponseEntity<List<BadgeDTO>> getAvailableBadges() {
        List<BadgeDTO> badges = badgeService.getAllAvailableBadges();
        return ResponseEntity.ok(badges);
    }
    
    @PostMapping("/badges/{badgeId}/toggle-display")
    public ResponseEntity<String> toggleBadgeDisplay(
            @PathVariable Long badgeId,
            Authentication authentication) {
        // Implementation would toggle badge display status
        return ResponseEntity.ok("Badge display toggled successfully");
    }
    
    @PostMapping("/initialize-badges")
    public ResponseEntity<String> initializeBadges() {
        badgeService.initializeDefaultBadges();
        return ResponseEntity.ok("Default badges initialized successfully");
    }
}
