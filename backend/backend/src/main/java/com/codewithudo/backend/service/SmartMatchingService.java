package com.codewithudo.backend.service;

import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartMatchingService {
    
    private final UserRepository userRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final UserPreferenceProfileRepository preferenceProfileRepository;
    private final MatchRepository matchRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public List<User> findSmartMatches(Long userId, int limit) {
        log.info("Finding smart matches for user: {}", userId);
        
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user's preference profile or create one
        UserPreferenceProfile profile = getOrCreatePreferenceProfile(currentUser);
        
        // Get all potential matches (exclude already matched users)
        List<User> potentialMatches = getPotentialMatches(userId);
        
        // Calculate compatibility scores
        List<MatchCandidate> candidates = potentialMatches.stream()
            .map(user -> calculateCompatibility(currentUser, user, profile))
            .filter(candidate -> candidate.getScore() > 0.3) // Minimum compatibility threshold
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());
        
        log.info("Found {} smart matches for user {}", candidates.size(), userId);
        
        return candidates.stream()
            .map(MatchCandidate::getUser)
            .collect(Collectors.toList());
    }
    
    private UserPreferenceProfile getOrCreatePreferenceProfile(User user) {
        return preferenceProfileRepository.findByUserId(user.getId())
            .orElseGet(() -> createInitialPreferenceProfile(user));
    }
    
    private UserPreferenceProfile createInitialPreferenceProfile(User user) {
        UserPreferenceProfile profile = new UserPreferenceProfile();
        profile.setUserId(user.getId());
        profile.setSkillVector(generateInitialVector(user.getSkills()));
        profile.setInterestVector(generateInitialVector(user.getInterests()));
        profile.setIndustryVector(generateInitialVector(user.getIndustry()));
        profile.setCommunicationStyle(UserPreferenceProfile.CommunicationStyle.COLLABORATIVE);
        profile.setMeetingPreference(UserPreferenceProfile.MeetingPreference.NO_PREFERENCE);
        profile.setExperienceLevel(UserPreferenceProfile.ExperienceLevel.MID);
        profile.setLastUpdated(LocalDateTime.now());
        
        return preferenceProfileRepository.save(profile);
    }
    
    private String generateInitialVector(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "[]";
        }
        
        // Simple keyword extraction and weighting
        String[] keywords = text.toLowerCase().split("[,\\s]+");
        Map<String, Double> vector = new HashMap<>();
        
        for (String keyword : keywords) {
            keyword = keyword.trim();
            if (!keyword.isEmpty()) {
                vector.put(keyword, vector.getOrDefault(keyword, 0.0) + 1.0);
            }
        }
        
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (Exception e) {
            log.error("Error generating vector for text: {}", text, e);
            return "[]";
        }
    }
    
    private List<User> getPotentialMatches(Long userId) {
        // Get users who haven't been matched with current user
        List<Long> excludedUserIds = matchRepository.findMatchedUserIds(userId);
        excludedUserIds.add(userId); // Exclude self
        
        return userRepository.findActiveUsersExcluding(excludedUserIds);
    }
    
    private MatchCandidate calculateCompatibility(User currentUser, User candidate, UserPreferenceProfile profile) {
        double score = 0.0;
        Map<String, Double> factors = new HashMap<>();
        
        // Industry compatibility (20% weight)
        double industryScore = calculateIndustryCompatibility(currentUser.getIndustry(), candidate.getIndustry());
        factors.put("industry", industryScore);
        score += industryScore * 0.2;
        
        // Skills compatibility (25% weight)
        double skillsScore = calculateTextSimilarity(currentUser.getSkills(), candidate.getSkills());
        factors.put("skills", skillsScore);
        score += skillsScore * 0.25;
        
        // Interests compatibility (25% weight)
        double interestsScore = calculateTextSimilarity(currentUser.getInterests(), candidate.getInterests());
        factors.put("interests", interestsScore);
        score += interestsScore * 0.25;
        
        // Experience level compatibility (15% weight)
        double experienceScore = calculateExperienceCompatibility(profile, candidate);
        factors.put("experience", experienceScore);
        score += experienceScore * 0.15;
        
        // Interaction history boost (10% weight)
        double historyScore = calculateHistoryBoost(currentUser.getId(), candidate.getId());
        factors.put("history", historyScore);
        score += historyScore * 0.1;
        
        // Diversity factor (5% weight) - encourage meeting different people
        double diversityScore = calculateDiversityScore(currentUser.getId(), candidate);
        factors.put("diversity", diversityScore);
        score += diversityScore * 0.05;
        
        return new MatchCandidate(candidate, Math.min(score, 1.0), factors);
    }
    
    private double calculateIndustryCompatibility(String industry1, String industry2) {
        if (industry1 == null || industry2 == null) return 0.5;
        if (industry1.equalsIgnoreCase(industry2)) return 1.0;
        
        // Related industries logic could be expanded
        return 0.3;
    }
    
    private double calculateTextSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        
        Set<String> words1 = Arrays.stream(text1.toLowerCase().split("[,\\s]+"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        
        Set<String> words2 = Arrays.stream(text2.toLowerCase().split("[,\\s]+"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        
        if (words1.isEmpty() || words2.isEmpty()) return 0.0;
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
    
    private double calculateExperienceCompatibility(UserPreferenceProfile profile, User candidate) {
        // Simple experience level matching - could be enhanced with ML
        return 0.7; // Default compatibility
    }
    
    private double calculateHistoryBoost(Long userId1, Long userId2) {
        List<UserInteraction> interactions = userInteractionRepository
            .findInteractionsBetweenUsers(userId1, userId2);
        
        // Boost score based on positive interactions
        long positiveInteractions = interactions.stream()
            .filter(i -> i.getInteractionType() == UserInteraction.InteractionType.PROFILE_VIEW ||
                        i.getInteractionType() == UserInteraction.InteractionType.MATCH_ACCEPTED)
            .count();
        
        return Math.min(positiveInteractions * 0.1, 0.3);
    }
    
    private double calculateDiversityScore(Long userId, User candidate) {
        // Encourage diversity in matching
        List<UserInteraction> recentMatches = userInteractionRepository
            .findRecentInteractions(userId, LocalDateTime.now().minusDays(30));
        
        // Check if candidate is from different industry/background
        boolean isDiverse = recentMatches.stream()
            .noneMatch(i -> i.getInteractionValue() != null && 
                          i.getInteractionValue().contains(candidate.getIndustry()));
        
        return isDiverse ? 0.8 : 0.2;
    }
    
    @Transactional
    public void recordInteraction(Long userId, Long targetUserId, 
                                UserInteraction.InteractionType type, String value) {
        UserInteraction interaction = new UserInteraction();
        interaction.setUserId(userId);
        interaction.setTargetUserId(targetUserId);
        interaction.setInteractionType(type);
        interaction.setInteractionValue(value);
        interaction.setWeight(getInteractionWeight(type));
        
        userInteractionRepository.save(interaction);
        
        // Update user preference profile based on interaction
        updatePreferenceProfile(userId, interaction);
    }
    
    private double getInteractionWeight(UserInteraction.InteractionType type) {
        return switch (type) {
            case MATCH_ACCEPTED -> 2.0;
            case MEETING_COMPLETED -> 3.0;
            case FEEDBACK_GIVEN -> 1.5;
            case PROFILE_VIEW -> 0.5;
            case MATCH_REJECTED -> -1.0;
            default -> 1.0;
        };
    }
    
    private void updatePreferenceProfile(Long userId, UserInteraction interaction) {
        // Update user preferences based on interactions
        // This would be enhanced with ML algorithms in production
        log.debug("Updating preference profile for user {} based on interaction {}", 
                 userId, interaction.getInteractionType());
    }
    
    public double calculateCompatibilityScore(Long userId1, Long userId2) {
        Optional<User> user1Opt = userRepository.findById(userId1);
        Optional<User> user2Opt = userRepository.findById(userId2);
        
        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            return 0.0;
        }
        
        User user1 = user1Opt.get();
        User user2 = user2Opt.get();
        
        // Simple compatibility calculation based on shared attributes
        double score = 0.0;
        
        // Industry match (30% weight)
        if (user1.getIndustry() != null && user1.getIndustry().equals(user2.getIndustry())) {
            score += 0.3;
        }
        
        // Skills overlap (25% weight)
        if (user1.getSkills() != null && user2.getSkills() != null) {
            String[] skills1 = user1.getSkills().toLowerCase().split(",");
            String[] skills2 = user2.getSkills().toLowerCase().split(",");
            int commonSkills = 0;
            for (String skill1 : skills1) {
                for (String skill2 : skills2) {
                    if (skill1.trim().equals(skill2.trim())) {
                        commonSkills++;
                    }
                }
            }
            if (skills1.length > 0 && skills2.length > 0) {
                score += 0.25 * (double) commonSkills / Math.max(skills1.length, skills2.length);
            }
        }
        
        // Interests overlap (25% weight)
        if (user1.getInterests() != null && user2.getInterests() != null) {
            String[] interests1 = user1.getInterests().toLowerCase().split(",");
            String[] interests2 = user2.getInterests().toLowerCase().split(",");
            int commonInterests = 0;
            for (String interest1 : interests1) {
                for (String interest2 : interests2) {
                    if (interest1.trim().equals(interest2.trim())) {
                        commonInterests++;
                    }
                }
            }
            if (interests1.length > 0 && interests2.length > 0) {
                score += 0.25 * (double) commonInterests / Math.max(interests1.length, interests2.length);
            }
        }
        
        // Company diversity bonus (20% weight)
        if (user1.getCompanyId() != null && user2.getCompanyId() != null && 
            !user1.getCompanyId().equals(user2.getCompanyId())) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
    
    // Inner class for match candidates
    private static class MatchCandidate {
        private final User user;
        private final double score;
        public MatchCandidate(User user, double score, Map<String, Double> factors) {
            this.user = user;
            this.score = score;
        }
        
        public User getUser() { return user; }
        public double getScore() { return score; }
    }
}
