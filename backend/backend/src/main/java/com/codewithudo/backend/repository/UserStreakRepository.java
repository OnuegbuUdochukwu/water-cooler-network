package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {
    
    List<UserStreak> findByUserId(Long userId);
    
    Optional<UserStreak> findByUserIdAndStreakType(Long userId, UserStreak.StreakType streakType);
    
    @Query("SELECT us FROM UserStreak us WHERE us.userId = :userId AND us.currentCount > 0")
    List<UserStreak> findActiveStreaksByUserId(@Param("userId") Long userId);
    
    @Query("SELECT us FROM UserStreak us WHERE us.bestCount >= :minCount")
    List<UserStreak> findByBestCountGreaterThanEqual(@Param("minCount") Integer minCount);
    
    @Query("SELECT us FROM UserStreak us WHERE us.streakType = :streakType ORDER BY us.currentCount DESC")
    List<UserStreak> findTopStreaksByType(@Param("streakType") UserStreak.StreakType streakType);
    
    @Query("SELECT COUNT(us) FROM UserStreak us WHERE us.userId = :userId AND us.currentCount > 0")
    Long countActiveStreaksByUserId(@Param("userId") Long userId);
}
