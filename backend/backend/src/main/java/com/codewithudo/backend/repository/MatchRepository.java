package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findByUser1IdOrUser2IdAndIsActiveTrue(Long user1Id, Long user2Id);
    
    List<Match> findByUser1IdAndStatusAndIsActiveTrue(Long userId, Match.MatchStatus status);
    
    List<Match> findByUser2IdAndStatusAndIsActiveTrue(Long userId, Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE (m.user1Id = :userId OR m.user2Id = :userId) AND m.status = :status AND m.isActive = true")
    List<Match> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE m.status = 'PENDING' AND m.isActive = true AND m.user1Id != :userId AND m.user2Id != :userId")
    List<Match> findAvailableMatchesForUser(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Match m WHERE m.status = 'SCHEDULED' AND m.scheduledTime BETWEEN :startTime AND :endTime AND m.isActive = true")
    List<Match> findScheduledMatchesInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    Optional<Match> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT DISTINCT CASE WHEN m.user1Id = :userId THEN m.user2Id ELSE m.user1Id END " +
           "FROM Match m WHERE (m.user1Id = :userId OR m.user2Id = :userId) AND m.isActive = true")
    List<Long> findMatchedUserIds(@Param("userId") Long userId);
    
    boolean existsByUser1IdAndUser2IdAndStatusAndIsActiveTrue(Long user1Id, Long user2Id, Match.MatchStatus status);
    
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Long countByUser1IdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    Long countByUser2IdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
