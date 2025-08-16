package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnalyticsRepository extends JpaRepository<UserAnalytics, Long> {
    
    Optional<UserAnalytics> findByUserIdAndDate(Long userId, LocalDate date);
    
    List<UserAnalytics> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<UserAnalytics> findByUserIdOrderByDateDesc(Long userId);
    
    List<UserAnalytics> findTop30ByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT ua FROM UserAnalytics ua WHERE ua.userId = :userId AND ua.date >= :startDate ORDER BY ua.date DESC")
    List<UserAnalytics> findUserAnalyticsSince(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT SUM(ua.sessionDurationMinutes) FROM UserAnalytics ua WHERE ua.userId = :userId AND ua.date BETWEEN :startDate AND :endDate")
    Integer getTotalSessionDuration(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(ua.matchesCompleted) FROM UserAnalytics ua WHERE ua.userId = :userId AND ua.date BETWEEN :startDate AND :endDate")
    Integer getTotalMatchesCompleted(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(ua.averageRatingReceived) FROM UserAnalytics ua WHERE ua.userId = :userId AND ua.averageRatingReceived > 0 AND ua.date BETWEEN :startDate AND :endDate")
    Double getAverageRatingReceived(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(DISTINCT ua.userId) FROM UserAnalytics ua WHERE ua.date = :date AND ua.loginCount > 0")
    Long getActiveUsersForDate(@Param("date") LocalDate date);
    
    @Query("SELECT ua.userId, SUM(ua.actionsPerformed) as totalActions FROM UserAnalytics ua WHERE ua.date BETWEEN :startDate AND :endDate GROUP BY ua.userId ORDER BY totalActions DESC")
    List<Object[]> getMostActiveUsers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ua.userId, AVG(ua.featureUsageScore) as avgScore FROM UserAnalytics ua WHERE ua.date BETWEEN :startDate AND :endDate GROUP BY ua.userId ORDER BY avgScore DESC")
    List<Object[]> getTopEngagedUsers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
