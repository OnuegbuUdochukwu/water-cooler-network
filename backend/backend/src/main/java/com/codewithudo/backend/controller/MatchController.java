package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.MatchDto;
import com.codewithudo.backend.dto.MatchRequestDto;
import com.codewithudo.backend.dto.MatchResponseDto;
import com.codewithudo.backend.dto.UserPreferencesDto;
import com.codewithudo.backend.entity.Match;
import com.codewithudo.backend.service.MatchingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {
    
    @Autowired
    private MatchingService matchingService;
    
    @PostMapping("/request")
    public ResponseEntity<MatchDto> createMatchRequest(@Valid @RequestBody MatchRequestDto requestDto) {
        try {
            Long currentUserId = getCurrentUserId();
            MatchDto match = matchingService.createMatchRequest(currentUserId, requestDto);
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{matchId}/respond")
    public ResponseEntity<MatchDto> respondToMatch(@PathVariable Long matchId, 
                                                 @Valid @RequestBody MatchResponseDto responseDto) {
        try {
            Long currentUserId = getCurrentUserId();
            MatchDto match = matchingService.respondToMatch(matchId, currentUserId, responseDto);
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<MatchDto>> getAvailableMatches() {
        try {
            Long currentUserId = getCurrentUserId();
            List<MatchDto> matches = matchingService.getAvailableMatches(currentUserId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my-matches")
    public ResponseEntity<List<MatchDto>> getMyMatches() {
        try {
            Long currentUserId = getCurrentUserId();
            List<MatchDto> matches = matchingService.getUserMatches(currentUserId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my-matches/{status}")
    public ResponseEntity<List<MatchDto>> getMyMatchesByStatus(@PathVariable String status) {
        try {
            Long currentUserId = getCurrentUserId();
            Match.MatchStatus matchStatus = Match.MatchStatus.valueOf(status.toUpperCase());
            List<MatchDto> matches = matchingService.getUserMatchesByStatus(currentUserId, matchStatus);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> getUserPreferences() {
        try {
            Long currentUserId = getCurrentUserId();
            UserPreferencesDto preferences = matchingService.getUserPreferences(currentUserId);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> updateUserPreferences(@Valid @RequestBody UserPreferencesDto preferencesDto) {
        try {
            Long currentUserId = getCurrentUserId();
            UserPreferencesDto updatedPreferences = matchingService.updateUserPreferences(currentUserId, preferencesDto);
            return ResponseEntity.ok(updatedPreferences);
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
