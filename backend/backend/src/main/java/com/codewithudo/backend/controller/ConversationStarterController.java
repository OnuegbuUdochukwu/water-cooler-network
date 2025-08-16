package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.ConversationStarterDTO;
import com.codewithudo.backend.entity.ConversationStarter;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.service.ConversationStarterService;
import com.codewithudo.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversation-starters")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ConversationStarterController {
    
    private final ConversationStarterService conversationStarterService;
    private final UserService userService;
    
    @GetMapping("/personalized/{targetUserId}")
    public ResponseEntity<List<ConversationStarterDTO>> getPersonalizedStarters(
            Authentication authentication,
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "5") int count) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        List<ConversationStarter> starters = conversationStarterService
            .generatePersonalizedStarters(currentUser.getId(), targetUserId, count);
        
        List<ConversationStarterDTO> starterDTOs = starters.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(starterDTOs);
    }
    
    @PostMapping("/usage/{starterId}")
    public ResponseEntity<Void> recordStarterUsage(
            @PathVariable Long starterId,
            @RequestParam boolean wasSuccessful) {
        
        conversationStarterService.recordStarterUsage(starterId, wasSuccessful);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        // Return available categories
        List<String> categories = List.of(
            "Professional", "Skills", "Industry", "Interests", 
            "Career", "Learning", "Personal", "Icebreaker"
        );
        return ResponseEntity.ok(categories);
    }
    
    private ConversationStarterDTO convertToDTO(ConversationStarter starter) {
        ConversationStarterDTO dto = new ConversationStarterDTO();
        dto.setId(starter.getId());
        dto.setTemplate(starter.getTemplate());
        dto.setCategory(starter.getCategory());
        dto.setContextType(starter.getContextType().name());
        dto.setDifficultyLevel(starter.getDifficultyLevel());
        dto.setSuccessRate(starter.getSuccessRate());
        dto.setUsageCount(starter.getUsageCount());
        return dto;
    }
}
