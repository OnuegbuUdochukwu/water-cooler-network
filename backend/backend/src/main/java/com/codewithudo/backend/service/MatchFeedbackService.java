package com.codewithudo.backend.service;

import com.codewithudo.backend.entity.Match;
import com.codewithudo.backend.entity.MatchFeedback;
import com.codewithudo.backend.entity.UserInteraction;
import com.codewithudo.backend.repository.MatchFeedbackRepository;
import com.codewithudo.backend.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchFeedbackService {
    
    private final MatchFeedbackRepository matchFeedbackRepository;
    private final MatchRepository matchRepository;
    private final SmartMatchingService smartMatchingService;
    
    @Transactional
    public MatchFeedback submitFeedback(Long matchId, Long userId, Integer qualityRating,
                                       Integer conversationRating, Integer relevanceRating,
                                       Boolean wouldMeetAgain, String feedbackText,
                                       String improvementSuggestions, String tags) {
        
        // Validate match exists and user is part of it
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new RuntimeException("Match not found"));
        
        if (!match.getUser1Id().equals(userId) && !match.getUser2Id().equals(userId)) {
            throw new RuntimeException("User not authorized to provide feedback for this match");
        }
        
        // Check if feedback already exists
        Optional<MatchFeedback> existingFeedback = matchFeedbackRepository
            .findByMatchIdAndUserId(matchId, userId);
        
        MatchFeedback feedback;
        if (existingFeedback.isPresent()) {
            feedback = existingFeedback.get();
            log.info("Updating existing feedback for match {} by user {}", matchId, userId);
        } else {
            feedback = new MatchFeedback();
            feedback.setMatchId(matchId);
            feedback.setUserId(userId);
            log.info("Creating new feedback for match {} by user {}", matchId, userId);
        }
        
        feedback.setQualityRating(qualityRating);
        feedback.setConversationRating(conversationRating);
        feedback.setRelevanceRating(relevanceRating);
        feedback.setWouldMeetAgain(wouldMeetAgain);
        feedback.setFeedbackText(feedbackText);
        feedback.setImprovementSuggestions(improvementSuggestions);
        feedback.setTags(tags);
        
        MatchFeedback savedFeedback = matchFeedbackRepository.save(feedback);
        
        // Record interaction for ML learning
        Long targetUserId = match.getUser1Id().equals(userId) ? match.getUser2Id() : match.getUser1Id();
        smartMatchingService.recordInteraction(userId, targetUserId, 
            UserInteraction.InteractionType.FEEDBACK_GIVEN, 
            String.format("quality:%d,conversation:%d,relevance:%d", 
                qualityRating, conversationRating, relevanceRating));
        
        // Update match completion status if both users provided feedback
        updateMatchCompletionStatus(matchId);
        
        return savedFeedback;
    }
    
    private void updateMatchCompletionStatus(Long matchId) {
        List<MatchFeedback> allFeedback = matchFeedbackRepository.findByMatchId(matchId);
        
        if (allFeedback.size() >= 2) {
            // Both users provided feedback, mark match as fully completed
            Match match = matchRepository.findById(matchId).orElse(null);
            if (match != null && match.getStatus() == Match.MatchStatus.COMPLETED) {
                // Calculate average compatibility score from feedback
                double avgQuality = allFeedback.stream()
                    .mapToInt(MatchFeedback::getQualityRating)
                    .average()
                    .orElse(0.0);
                
                match.setCompatibilityScore(avgQuality / 5.0); // Normalize to 0-1 scale
                matchRepository.save(match);
            }
        }
    }
    
    public List<MatchFeedback> getMatchFeedback(Long matchId) {
        return matchFeedbackRepository.findByMatchId(matchId);
    }
    
    public List<MatchFeedback> getUserFeedback(Long userId) {
        return matchFeedbackRepository.findByUserId(userId);
    }
    
    public Double getUserAverageMatchQuality(Long userId) {
        return matchFeedbackRepository.getAverageUserMatchQuality(userId);
    }
    
    public MatchQualityStats getOverallMatchQualityStats() {
        Long totalFeedback = matchFeedbackRepository.count();
        Long positiveFeedback = matchFeedbackRepository.countPositiveFeedback();
        Long highQualityMatches = matchFeedbackRepository.countHighQualityMatches(4);
        
        double positiveRate = totalFeedback > 0 ? (double) positiveFeedback / totalFeedback : 0.0;
        double highQualityRate = totalFeedback > 0 ? (double) highQualityMatches / totalFeedback : 0.0;
        
        return new MatchQualityStats(totalFeedback, positiveFeedback, highQualityMatches, 
                                   positiveRate, highQualityRate);
    }
    
    public List<String> getPopularFeedbackTags() {
        return matchFeedbackRepository.getAllFeedbackTags();
    }
    
    // Inner class for match quality statistics
    public static class MatchQualityStats {
        private final Long totalFeedback;
        private final Long positiveFeedback;
        private final Long highQualityMatches;
        private final Double positiveRate;
        private final Double highQualityRate;
        
        public MatchQualityStats(Long totalFeedback, Long positiveFeedback, Long highQualityMatches,
                               Double positiveRate, Double highQualityRate) {
            this.totalFeedback = totalFeedback;
            this.positiveFeedback = positiveFeedback;
            this.highQualityMatches = highQualityMatches;
            this.positiveRate = positiveRate;
            this.highQualityRate = highQualityRate;
        }
        
        public Long getTotalFeedback() { return totalFeedback; }
        public Long getPositiveFeedback() { return positiveFeedback; }
        public Long getHighQualityMatches() { return highQualityMatches; }
        public Double getPositiveRate() { return positiveRate; }
        public Double getHighQualityRate() { return highQualityRate; }
    }
}
