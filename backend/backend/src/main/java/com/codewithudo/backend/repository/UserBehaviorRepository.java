package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    List<UserBehavior> findByUserIdOrderByTimestampDesc(Long userId);

    List<UserBehavior> findByUserIdAndBehaviorTypeOrderByTimestampDesc(Long userId, UserBehavior.BehaviorType behaviorType);

    List<UserBehavior> findByBehaviorTypeOrderByTimestampDesc(UserBehavior.BehaviorType behaviorType);

    List<UserBehavior> findByUserIdAndTimestampBetweenOrderByTimestampDesc(
            Long userId, LocalDateTime startTime, LocalDateTime endTime);

    List<UserBehavior> findByBehaviorTypeAndTimestampBetweenOrderByTimestampDesc(
            UserBehavior.BehaviorType behaviorType, LocalDateTime startTime, LocalDateTime endTime);

    List<UserBehavior> findByUserIdAndTargetTypeOrderByTimestampDesc(Long userId, String targetType);

    List<UserBehavior> findByUserIdAndTargetIdAndTargetTypeOrderByTimestampDesc(
            Long userId, Long targetId, String targetType);

    @Query("SELECT b FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.timestamp >= :startTime ORDER BY b.timestamp DESC")
    List<UserBehavior> findRecentBehaviorsByUser(
            @Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT b FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.behaviorType IN :behaviorTypes ORDER BY b.timestamp DESC")
    List<UserBehavior> findBehaviorsByUserAndTypes(
            @Param("userId") Long userId, @Param("behaviorTypes") List<UserBehavior.BehaviorType> behaviorTypes);

    @Query("SELECT COUNT(b) FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.behaviorType = :behaviorType")
    Long countBehaviorsByUserAndType(
            @Param("userId") Long userId, @Param("behaviorType") UserBehavior.BehaviorType behaviorType);

    @Query("SELECT COUNT(b) FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.behaviorType = :behaviorType AND b.timestamp >= :startTime")
    Long countRecentBehaviorsByUserAndType(
            @Param("userId") Long userId, 
            @Param("behaviorType") UserBehavior.BehaviorType behaviorType,
            @Param("startTime") LocalDateTime startTime);

    @Query("SELECT b.behaviorType, COUNT(b) FROM UserBehavior b WHERE b.userId = :userId " +
           "GROUP BY b.behaviorType ORDER BY COUNT(b) DESC")
    List<Object[]> getBehaviorDistributionByUser(@Param("userId") Long userId);

    @Query("SELECT b FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.behaviorType = :behaviorType AND b.intensityScore >= :minIntensity " +
           "ORDER BY b.intensityScore DESC")
    List<UserBehavior> findHighIntensityBehaviorsByUser(
            @Param("userId") Long userId,
            @Param("behaviorType") UserBehavior.BehaviorType behaviorType,
            @Param("minIntensity") Double minIntensity);

    @Query("SELECT AVG(b.durationSeconds) FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.behaviorType = :behaviorType AND b.durationSeconds IS NOT NULL")
    Double getAverageDurationByUserAndBehaviorType(
            @Param("userId") Long userId, @Param("behaviorType") UserBehavior.BehaviorType behaviorType);

    @Query("SELECT b FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.sessionId = :sessionId ORDER BY b.timestamp ASC")
    List<UserBehavior> findBehaviorsByUserAndSession(
            @Param("userId") Long userId, @Param("sessionId") String sessionId);

    @Query("SELECT DISTINCT b.sessionId FROM UserBehavior b WHERE b.userId = :userId " +
           "AND b.sessionId IS NOT NULL ORDER BY b.timestamp DESC")
    List<String> findSessionIdsByUser(@Param("userId") Long userId);
}
