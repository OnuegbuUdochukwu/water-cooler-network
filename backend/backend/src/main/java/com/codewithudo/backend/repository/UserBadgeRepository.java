package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    
    List<UserBadge> findByUserId(Long userId);
    
    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.userId = :userId")
    List<UserBadge> findByUserIdWithBadge(@Param("userId") Long userId);
    
    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.userId = :userId AND ub.isDisplayed = true")
    List<UserBadge> findDisplayedBadgesByUserId(@Param("userId") Long userId);
    
    Optional<UserBadge> findByUserIdAndBadgeId(Long userId, Long badgeId);
    
    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.userId = :userId")
    Long countBadgesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ub FROM UserBadge ub WHERE ub.notificationSent = false")
    List<UserBadge> findUnnotifiedBadges();
    
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);
}
