package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.SmartMatchDTO;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.service.SmartMatchingService;
import com.codewithudo.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/smart-matching")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class SmartMatchingController {
    
    private final SmartMatchingService smartMatchingService;
    private final UserService userService;
    
    @GetMapping("/recommendations")
    public ResponseEntity<List<SmartMatchDTO>> getSmartMatches(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        List<User> matches = smartMatchingService.findSmartMatches(currentUser.getId(), limit);
        
        List<SmartMatchDTO> matchDTOs = matches.stream()
            .map(this::convertToSmartMatchDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(matchDTOs);
    }
    
    @PostMapping("/interaction")
    public ResponseEntity<Void> recordInteraction(
            Authentication authentication,
            @RequestParam Long targetUserId,
            @RequestParam String interactionType,
            @RequestParam(required = false) String value) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        try {
            smartMatchingService.recordInteraction(
                currentUser.getId(), 
                targetUserId, 
                com.codewithudo.backend.entity.UserInteraction.InteractionType.valueOf(interactionType.toUpperCase()),
                value
            );
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/compatibility/{userId}")
    public ResponseEntity<Double> getCompatibilityScore(
            Authentication authentication,
            @PathVariable Long userId) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        double score = smartMatchingService.calculateCompatibilityScore(currentUser.getId(), userId);
        return ResponseEntity.ok(score);
    }
    
    private SmartMatchDTO convertToSmartMatchDTO(User user) {
        SmartMatchDTO dto = new SmartMatchDTO();
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setIndustry(user.getIndustry());
        dto.setSkills(user.getSkills());
        dto.setInterests(user.getInterests());
        dto.setLinkedinUrl(user.getLinkedinUrl());
        dto.setCompatibilityScore(0.8); // Placeholder - would be calculated
        dto.setMatchReason("Based on shared interests and complementary skills");
        return dto;
    }
}
