package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.*;
import com.codewithudo.backend.service.LoungeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lounges")
@CrossOrigin(origins = "*")
public class LoungeController {
    
    @Autowired
    private LoungeService loungeService;
    
    @PostMapping
    public ResponseEntity<LoungeDto> createLounge(@Valid @RequestBody CreateLoungeDto createDto) {
        try {
            Long currentUserId = getCurrentUserId();
            LoungeDto lounge = loungeService.createLounge(currentUserId, createDto);
            return ResponseEntity.ok(lounge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{loungeId}")
    public ResponseEntity<LoungeDto> getLoungeById(@PathVariable Long loungeId) {
        try {
            Long currentUserId = getCurrentUserId();
            LoungeDto lounge = loungeService.getLoungeById(loungeId, currentUserId);
            return ResponseEntity.ok(lounge);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<LoungeDto>> getAllLounges() {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeDto> lounges = loungeService.getAllLounges(currentUserId);
            return ResponseEntity.ok(lounges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<LoungeDto>> searchLounges(@RequestParam String q) {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeDto> lounges = loungeService.searchLounges(q, currentUserId);
            return ResponseEntity.ok(lounges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<LoungeDto>> getLoungesByTopic(@PathVariable String topic) {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeDto> lounges = loungeService.getLoungesByTopic(topic, currentUserId);
            return ResponseEntity.ok(lounges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<LoungeDto>> getLoungesByCategory(@PathVariable String category) {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeDto> lounges = loungeService.getLoungesByCategory(category, currentUserId);
            return ResponseEntity.ok(lounges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<LoungeDto>> getFeaturedLounges() {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeDto> lounges = loungeService.getFeaturedLounges(currentUserId);
            return ResponseEntity.ok(lounges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my-lounges")
    public ResponseEntity<List<LoungeDto>> getUserLounges() {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeDto> lounges = loungeService.getUserLounges(currentUserId);
            return ResponseEntity.ok(lounges);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{loungeId}/join")
    public ResponseEntity<LoungeDto> joinLounge(@PathVariable Long loungeId) {
        try {
            Long currentUserId = getCurrentUserId();
            LoungeDto lounge = loungeService.joinLounge(loungeId, currentUserId);
            return ResponseEntity.ok(lounge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{loungeId}/leave")
    public ResponseEntity<Void> leaveLounge(@PathVariable Long loungeId) {
        try {
            Long currentUserId = getCurrentUserId();
            loungeService.leaveLounge(loungeId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{loungeId}/messages")
    public ResponseEntity<List<LoungeMessageDto>> getLoungeMessages(@PathVariable Long loungeId) {
        try {
            Long currentUserId = getCurrentUserId();
            List<LoungeMessageDto> messages = loungeService.getLoungeMessages(loungeId, currentUserId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{loungeId}/messages")
    public ResponseEntity<LoungeMessageDto> sendMessage(@PathVariable Long loungeId, @Valid @RequestBody SendMessageDto sendDto) {
        try {
            Long currentUserId = getCurrentUserId();
            sendDto.setLoungeId(loungeId);
            LoungeMessageDto message = loungeService.sendMessage(currentUserId, sendDto);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{loungeId}")
    public ResponseEntity<Void> deleteLounge(@PathVariable Long loungeId) {
        try {
            Long currentUserId = getCurrentUserId();
            loungeService.deleteLounge(loungeId, currentUserId);
            return ResponseEntity.ok().build();
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
