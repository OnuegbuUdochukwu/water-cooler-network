package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.PlatformAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformAnalyticsRepository extends JpaRepository<PlatformAnalytics, Long> {
    
    Optional<PlatformAnalytics> findByDate(LocalDate date);
    
    List<PlatformAnalytics> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
    
    List<PlatformAnalytics> findTop30ByOrderByDateDesc();
    
    @Query("SELECT p FROM PlatformAnalytics p WHERE p.date >= :startDate ORDER BY p.date DESC")
    List<PlatformAnalytics> findAnalyticsSince(@Param("startDate") LocalDate startDate);
    
    @Query("SELECT AVG(p.matchSuccessRate) FROM PlatformAnalytics p WHERE p.date BETWEEN :startDate AND :endDate")
    Double getAverageMatchSuccessRate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(p.userGrowthRate) FROM PlatformAnalytics p WHERE p.date BETWEEN :startDate AND :endDate")
    Double getAverageUserGrowthRate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.newUsersToday) FROM PlatformAnalytics p WHERE p.date BETWEEN :startDate AND :endDate")
    Long getTotalNewUsers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.matchesCreatedToday) FROM PlatformAnalytics p WHERE p.date BETWEEN :startDate AND :endDate")
    Long getTotalMatchesCreated(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.meetingsCompletedToday) FROM PlatformAnalytics p WHERE p.date BETWEEN :startDate AND :endDate")
    Long getTotalMeetingsCompleted(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
