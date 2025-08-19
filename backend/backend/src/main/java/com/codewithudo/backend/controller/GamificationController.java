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
    public ResponseEntity<Void> toggleBadgeDisplay(@PathVariable Long badgeId, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        badgeService.toggleBadgeDisplay(userId, badgeId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/initialize-badges")
    public ResponseEntity<String> initializeBadges() {
        badgeService.initializeDefaultBadges();
        return ResponseEntity.ok("Default badges initialized successfully");
    }
    
    @GetMapping("/badges/{badgeId}/progress")
    public ResponseEntity<BadgeProgressDTO> getBadgeProgress(@PathVariable Long badgeId, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        BadgeProgressDTO progress = badgeService.getBadgeProgress(userId, badgeId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/badges/category/{category}")
    public ResponseEntity<List<BadgeDTO>> getBadgesByCategory(@PathVariable String category) {
        try {
            com.codewithudo.backend.entity.Badge.BadgeCategory badgeCategory = com.codewithudo.backend.entity.Badge.BadgeCategory.valueOf(category.toUpperCase());
            List<BadgeDTO> badges = badgeService.getBadgesByCategory(badgeCategory);
            return ResponseEntity.ok(badges);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/badges/rarity/{rarityLevel}")
    public ResponseEntity<List<BadgeDTO>> getBadgesByRarity(@PathVariable Integer rarityLevel) {
        if (rarityLevel < 1 || rarityLevel > 4) {
            return ResponseEntity.badRequest().build();
        }
        List<BadgeDTO> badges = badgeService.getBadgesByRarity(rarityLevel);
        return ResponseEntity.ok(badges);
    }
    
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntryDTO> leaderboard = gamificationService.getLeaderboard(limit);
        return ResponseEntity.ok(leaderboard);
    }
}
