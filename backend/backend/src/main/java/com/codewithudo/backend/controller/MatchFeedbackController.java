package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.MatchFeedbackDTO;
import com.codewithudo.backend.entity.MatchFeedback;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.service.MatchFeedbackService;
import com.codewithudo.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/match-feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class MatchFeedbackController {
    
    private final MatchFeedbackService matchFeedbackService;
    private final UserService userService;
    
    @PostMapping("/submit")
    public ResponseEntity<MatchFeedbackDTO> submitFeedback(
            Authentication authentication,
            @RequestParam Long matchId,
            @RequestParam Integer qualityRating,
            @RequestParam(required = false) Integer conversationRating,
            @RequestParam(required = false) Integer relevanceRating,
            @RequestParam(required = false) Boolean wouldMeetAgain,
            @RequestParam(required = false) String feedbackText,
            @RequestParam(required = false) String improvementSuggestions,
            @RequestParam(required = false) String tags) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        MatchFeedback feedback = matchFeedbackService.submitFeedback(
            matchId, currentUser.getId(), qualityRating, conversationRating,
            relevanceRating, wouldMeetAgain, feedbackText, improvementSuggestions, tags);
        
        return ResponseEntity.ok(convertToDTO(feedback));
    }
    
    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<MatchFeedbackDTO>> getMatchFeedback(@PathVariable Long matchId) {
        List<MatchFeedback> feedback = matchFeedbackService.getMatchFeedback(matchId);
        
        List<MatchFeedbackDTO> feedbackDTOs = feedback.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(feedbackDTOs);
    }
    
    @GetMapping("/my-feedback")
    public ResponseEntity<List<MatchFeedbackDTO>> getMyFeedback(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        List<MatchFeedback> feedback = matchFeedbackService.getUserFeedback(currentUser.getId());
        
        List<MatchFeedbackDTO> feedbackDTOs = feedback.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(feedbackDTOs);
    }
    
    @GetMapping("/my-average-quality")
    public ResponseEntity<Double> getMyAverageQuality(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        Double averageQuality = matchFeedbackService.getUserAverageMatchQuality(currentUser.getId());
        return ResponseEntity.ok(averageQuality != null ? averageQuality : 0.0);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<MatchFeedbackService.MatchQualityStats> getOverallStats() {
        MatchFeedbackService.MatchQualityStats stats = matchFeedbackService.getOverallMatchQualityStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/popular-tags")
    public ResponseEntity<List<String>> getPopularTags() {
        List<String> tags = matchFeedbackService.getPopularFeedbackTags();
        return ResponseEntity.ok(tags);
    }
    
    private MatchFeedbackDTO convertToDTO(MatchFeedback feedback) {
        MatchFeedbackDTO dto = new MatchFeedbackDTO();
        dto.setId(feedback.getId());
        dto.setMatchId(feedback.getMatchId());
        dto.setUserId(feedback.getUserId());
        dto.setQualityRating(feedback.getQualityRating());
        dto.setConversationRating(feedback.getConversationRating());
        dto.setRelevanceRating(feedback.getRelevanceRating());
        dto.setWouldMeetAgain(feedback.getWouldMeetAgain());
        dto.setFeedbackText(feedback.getFeedbackText());
        dto.setImprovementSuggestions(feedback.getImprovementSuggestions());
        dto.setTags(feedback.getTags());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
