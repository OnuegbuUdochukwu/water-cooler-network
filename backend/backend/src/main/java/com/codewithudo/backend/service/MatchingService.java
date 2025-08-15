package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.MatchDto;
import com.codewithudo.backend.dto.MatchRequestDto;
import com.codewithudo.backend.dto.MatchResponseDto;
import com.codewithudo.backend.dto.UserPreferencesDto;
import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    
    public MatchDto createMatchRequest(Long requestingUserId, MatchRequestDto requestDto) {
        // Check if match already exists
        if (matchRepository.existsByUser1IdAndUser2IdAndStatusAndIsActiveTrue(
                requestingUserId, requestDto.getTargetUserId(), Match.MatchStatus.PENDING)) {
            throw new RuntimeException("Match request already exists");
        }
        
        // Create new match
        Match match = new Match();
        match.setUser1Id(requestingUserId);
        match.setUser2Id(requestDto.getTargetUserId());
        match.setMatchType(requestDto.getMatchType());
        match.setStatus(Match.MatchStatus.PENDING);
        match.setScheduledTime(requestDto.getPreferredTime());
        match.setDurationMinutes(requestDto.getDurationMinutes());
        match.setMatchReason(requestDto.getMessage());
        match.setCompatibilityScore(calculateCompatibilityScore(requestingUserId, requestDto.getTargetUserId()));
        
        Match savedMatch = matchRepository.save(match);
        
        // Log the match request
        logChatMessage(savedMatch.getId(), requestingUserId, 
                "Match request sent: " + requestDto.getMessage(), 
                ChatHistory.MessageType.MATCH_REQUEST);
        
        return convertToMatchDto(savedMatch);
    }
    
    public MatchDto respondToMatch(Long matchId, Long respondingUserId, MatchResponseDto responseDto) {
        Match match = matchRepository.findByIdAndIsActiveTrue(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        
        // Verify the responding user is the target of the match
        if (!match.getUser2Id().equals(respondingUserId)) {
            throw new RuntimeException("User not authorized to respond to this match");
        }
        
        if (match.getStatus() != Match.MatchStatus.PENDING) {
            throw new RuntimeException("Match is not in pending status");
        }
        
        // Update match status
        match.setStatus(responseDto.getStatus());
        if (responseDto.getStatus() == Match.MatchStatus.ACCEPTED) {
            match.setScheduledTime(responseDto.getScheduledTime());
            match.setDurationMinutes(responseDto.getDurationMinutes());
        }
        
        Match updatedMatch = matchRepository.save(match);
        
        // Log the response
        String responseMessage = responseDto.getStatus() == Match.MatchStatus.ACCEPTED ? 
                "Match accepted" : "Match rejected";
        logChatMessage(matchId, respondingUserId, responseMessage, 
                responseDto.getStatus() == Match.MatchStatus.ACCEPTED ? 
                        ChatHistory.MessageType.MATCH_ACCEPTED : 
                        ChatHistory.MessageType.MATCH_REJECTED);
        
        return convertToMatchDto(updatedMatch);
    }
    
    public List<MatchDto> getAvailableMatches(Long userId) {
        List<UserPreferences> availableUsers = userPreferencesRepository.findAvailableUsersForMatching(userId);
        List<MatchDto> matches = new ArrayList<>();
        
        for (UserPreferences userPref : availableUsers) {
            if (userPref.getUserId().equals(userId)) continue;
            
            double compatibilityScore = calculateCompatibilityScore(userId, userPref.getUserId());
            if (compatibilityScore > 0.3) { // Minimum compatibility threshold
                MatchDto matchDto = new MatchDto();
                matchDto.setUser1Id(userId);
                matchDto.setUser2Id(userPref.getUserId());
                matchDto.setCompatibilityScore(compatibilityScore);
                matchDto.setMatchType(Match.MatchType.COFFEE_CHAT);
                matchDto.setStatus(Match.MatchStatus.PENDING);
                matches.add(matchDto);
            }
        }
        
        // Sort by compatibility score
        matches.sort((m1, m2) -> Double.compare(m2.getCompatibilityScore(), m1.getCompatibilityScore()));
        
        return matches;
    }
    
    public List<MatchDto> getUserMatches(Long userId) {
        List<Match> matches = matchRepository.findByUser1IdOrUser2IdAndIsActiveTrue(userId, userId);
        return matches.stream()
                .map(this::convertToMatchDto)
                .collect(Collectors.toList());
    }
    
    public List<MatchDto> getUserMatchesByStatus(Long userId, Match.MatchStatus status) {
        List<Match> matches = matchRepository.findByUserIdAndStatus(userId, status);
        return matches.stream()
                .map(this::convertToMatchDto)
                .collect(Collectors.toList());
    }
    
    public UserPreferencesDto getUserPreferences(Long userId) {
        UserPreferences prefs = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());
        prefs.setUserId(userId);
        
        return convertToPreferencesDto(prefs);
    }
    
    public UserPreferencesDto updateUserPreferences(Long userId, UserPreferencesDto preferencesDto) {
        UserPreferences prefs = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());
        
        prefs.setUserId(userId);
        prefs.setPreferredIndustries(preferencesDto.getPreferredIndustries());
        prefs.setPreferredRoles(preferencesDto.getPreferredRoles());
        prefs.setPreferredExperienceLevel(preferencesDto.getPreferredExperienceLevel());
        prefs.setMaxMatchDistanceKm(preferencesDto.getMaxMatchDistanceKm());
        prefs.setPreferredChatDuration(preferencesDto.getPreferredChatDuration());
        prefs.setAvailabilityStartTime(preferencesDto.getAvailabilityStartTime());
        prefs.setAvailabilityEndTime(preferencesDto.getAvailabilityEndTime());
        prefs.setPreferredTimezone(preferencesDto.getPreferredTimezone());
        prefs.setIsAvailableForMatching(preferencesDto.getIsAvailableForMatching());
        prefs.setAutoAcceptMatches(preferencesDto.getAutoAcceptMatches());
        prefs.setNotificationPreferences(preferencesDto.getNotificationPreferences());
        
        UserPreferences savedPrefs = userPreferencesRepository.save(prefs);
        return convertToPreferencesDto(savedPrefs);
    }
    
    private double calculateCompatibilityScore(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id).orElse(null);
        User user2 = userRepository.findById(user2Id).orElse(null);
        
        if (user1 == null || user2 == null) return 0.0;
        
        double score = 0.0;
        
        // Industry compatibility (40% weight)
        if (user1.getIndustry() != null && user2.getIndustry() != null) {
            if (user1.getIndustry().equalsIgnoreCase(user2.getIndustry())) {
                score += 0.4;
            } else if (user1.getIndustry() != null && user2.getIndustry() != null) {
                // Partial industry match (e.g., Technology vs Software)
                if (user1.getIndustry().toLowerCase().contains("tech") && 
                    user2.getIndustry().toLowerCase().contains("tech")) {
                    score += 0.2;
                }
            }
        }
        
        // Skills compatibility (30% weight)
        if (user1.getSkills() != null && user2.getSkills() != null) {
            Set<String> skills1 = Arrays.stream(user1.getSkills().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            Set<String> skills2 = Arrays.stream(user2.getSkills().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            
            Set<String> intersection = new HashSet<>(skills1);
            intersection.retainAll(skills2);
            
            if (!intersection.isEmpty()) {
                score += 0.3 * (double) intersection.size() / Math.max(skills1.size(), skills2.size());
            }
        }
        
        // Interests compatibility (20% weight)
        if (user1.getInterests() != null && user2.getInterests() != null) {
            Set<String> interests1 = Arrays.stream(user1.getInterests().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            Set<String> interests2 = Arrays.stream(user2.getInterests().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            
            Set<String> intersection = new HashSet<>(interests1);
            intersection.retainAll(interests2);
            
            if (!intersection.isEmpty()) {
                score += 0.2 * (double) intersection.size() / Math.max(interests1.size(), interests2.size());
            }
        }
        
        // Role compatibility (10% weight)
        if (user1.getRole() != null && user2.getRole() != null) {
            if (user1.getRole() == user2.getRole()) {
                score += 0.1;
            }
        }
        
        return Math.min(score, 1.0); // Cap at 1.0
    }
    
    private void logChatMessage(Long matchId, Long userId, String content, ChatHistory.MessageType messageType) {
        ChatHistory chatMessage = new ChatHistory();
        chatMessage.setMatchId(matchId);
        chatMessage.setUserId(userId);
        chatMessage.setContent(content);
        chatMessage.setMessageType(messageType);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setIsSystemMessage(messageType != ChatHistory.MessageType.TEXT);
        
        chatHistoryRepository.save(chatMessage);
    }
    
    private MatchDto convertToMatchDto(Match match) {
        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setUser1Id(match.getUser1Id());
        dto.setUser2Id(match.getUser2Id());
        dto.setMatchType(match.getMatchType());
        dto.setStatus(match.getStatus());
        dto.setMatchTime(match.getMatchTime());
        dto.setScheduledTime(match.getScheduledTime());
        dto.setDurationMinutes(match.getDurationMinutes());
        dto.setCompatibilityScore(match.getCompatibilityScore());
        dto.setMatchReason(match.getMatchReason());
        dto.setCreatedAt(match.getCreatedAt());
        dto.setUpdatedAt(match.getUpdatedAt());
        
        // Populate user names and emails
        userRepository.findById(match.getUser1Id()).ifPresent(user1 -> {
            dto.setUser1Name(user1.getName());
            dto.setUser1Email(user1.getEmail());
        });
        
        userRepository.findById(match.getUser2Id()).ifPresent(user2 -> {
            dto.setUser2Name(user2.getName());
            dto.setUser2Email(user2.getEmail());
        });
        
        return dto;
    }
    
    private UserPreferencesDto convertToPreferencesDto(UserPreferences prefs) {
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setPreferredIndustries(prefs.getPreferredIndustries());
        dto.setPreferredRoles(prefs.getPreferredRoles());
        dto.setPreferredExperienceLevel(prefs.getPreferredExperienceLevel());
        dto.setMaxMatchDistanceKm(prefs.getMaxMatchDistanceKm());
        dto.setPreferredChatDuration(prefs.getPreferredChatDuration());
        dto.setAvailabilityStartTime(prefs.getAvailabilityStartTime());
        dto.setAvailabilityEndTime(prefs.getAvailabilityEndTime());
        dto.setPreferredTimezone(prefs.getPreferredTimezone());
        dto.setIsAvailableForMatching(prefs.getIsAvailableForMatching());
        dto.setAutoAcceptMatches(prefs.getAutoAcceptMatches());
        dto.setNotificationPreferences(prefs.getNotificationPreferences());
        
        return dto;
    }
}
