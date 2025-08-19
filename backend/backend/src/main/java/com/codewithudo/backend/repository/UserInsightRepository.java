package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserInsightRepository extends JpaRepository<UserInsight, Long> {

    List<UserInsight> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserInsight> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<UserInsight> findByUserIdAndIsReadTrueOrderByCreatedAtDesc(Long userId);

    List<UserInsight> findByUserIdAndIsActionedFalseOrderByCreatedAtDesc(Long userId);

    List<UserInsight> findByUserIdAndIsActionedTrueOrderByCreatedAtDesc(Long userId);

    List<UserInsight> findByUserIdAndInsightTypeOrderByCreatedAtDesc(Long userId, UserInsight.InsightType insightType);

    List<UserInsight> findByInsightTypeOrderByCreatedAtDesc(UserInsight.InsightType insightType);

    List<UserInsight> findByUserIdAndCategoryOrderByCreatedAtDesc(Long userId, String category);

    List<UserInsight> findByUserIdAndPriorityLevelGreaterThanEqualOrderByCreatedAtDesc(Long userId, Integer priorityLevel);

    List<UserInsight> findByUserIdAndConfidenceScoreGreaterThanEqualOrderByCreatedAtDesc(Long userId, Double confidenceScore);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.createdAt >= :startTime ORDER BY i.createdAt DESC")
    List<UserInsight> findRecentInsightsByUser(
            @Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.expiresAt IS NULL OR i.expiresAt > :currentTime ORDER BY i.createdAt DESC")
    List<UserInsight> findActiveInsightsByUser(
            @Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.expiresAt <= :currentTime ORDER BY i.createdAt DESC")
    List<UserInsight> findExpiredInsightsByUser(
            @Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.feedbackRating IS NOT NULL ORDER BY i.feedbackRating DESC")
    List<UserInsight> findInsightsWithFeedbackByUser(@Param("userId") Long userId);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.feedbackRating IS NULL ORDER BY i.createdAt DESC")
    List<UserInsight> findInsightsWithoutFeedbackByUser(@Param("userId") Long userId);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.insightType IN :insightTypes ORDER BY i.createdAt DESC")
    List<UserInsight> findInsightsByUserAndTypes(
            @Param("userId") Long userId, @Param("insightTypes") List<UserInsight.InsightType> insightTypes);

    @Query("SELECT COUNT(i) FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.isRead = false")
    Long countUnreadInsightsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(i) FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.isActioned = true")
    Long countActionedInsightsByUser(@Param("userId") Long userId);

    @Query("SELECT i.insightType, COUNT(i) FROM UserInsight i WHERE i.userId = :userId " +
           "GROUP BY i.insightType ORDER BY COUNT(i) DESC")
    List<Object[]> getInsightTypeDistributionByUser(@Param("userId") Long userId);

    @Query("SELECT AVG(i.confidenceScore) FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.confidenceScore IS NOT NULL")
    Double getAverageConfidenceScoreByUser(@Param("userId") Long userId);

    @Query("SELECT AVG(i.feedbackRating) FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.feedbackRating IS NOT NULL")
    Double getAverageFeedbackRatingByUser(@Param("userId") Long userId);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.tags LIKE %:tag% ORDER BY i.createdAt DESC")
    List<UserInsight> findInsightsByUserAndTag(
            @Param("userId") Long userId, @Param("tag") String tag);

    @Query("SELECT i FROM UserInsight i WHERE i.userId = :userId " +
           "AND i.createdAt >= :startTime AND i.createdAt <= :endTime " +
           "ORDER BY i.createdAt DESC")
    List<UserInsight> findInsightsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
