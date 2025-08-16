package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.MatchFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchFeedbackRepository extends JpaRepository<MatchFeedback, Long> {
    
    List<MatchFeedback> findByMatchId(Long matchId);
    
    List<MatchFeedback> findByUserId(Long userId);
    
    Optional<MatchFeedback> findByMatchIdAndUserId(Long matchId, Long userId);
    
    @Query("SELECT AVG(mf.qualityRating) FROM MatchFeedback mf WHERE mf.matchId = :matchId")
    Double getAverageQualityRating(@Param("matchId") Long matchId);
    
    @Query("SELECT AVG(mf.qualityRating) FROM MatchFeedback mf " +
           "JOIN Match m ON mf.matchId = m.id " +
           "WHERE (m.user1Id = :userId OR m.user2Id = :userId)")
    Double getAverageUserMatchQuality(@Param("userId") Long userId);
    
    @Query("SELECT mf.tags FROM MatchFeedback mf WHERE mf.tags IS NOT NULL AND mf.tags != ''")
    List<String> getAllFeedbackTags();
    
    @Query("SELECT COUNT(mf) FROM MatchFeedback mf WHERE mf.wouldMeetAgain = true")
    Long countPositiveFeedback();
    
    @Query("SELECT COUNT(mf) FROM MatchFeedback mf WHERE mf.qualityRating >= :minRating")
    Long countHighQualityMatches(@Param("minRating") Integer minRating);
}
