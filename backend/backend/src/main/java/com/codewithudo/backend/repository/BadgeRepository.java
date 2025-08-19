package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    List<Badge> findByIsActiveTrue();
    
    List<Badge> findByBadgeType(Badge.BadgeType badgeType);
    
    List<Badge> findByBadgeCategory(Badge.BadgeCategory badgeCategory);
    
    @Query("SELECT b FROM Badge b WHERE b.isActive = true AND b.badgeType = :badgeType")
    List<Badge> findActiveBadgesByType(@Param("badgeType") Badge.BadgeType badgeType);
    
    @Query("SELECT b FROM Badge b WHERE b.isActive = true ORDER BY b.rarityLevel DESC, b.name ASC")
    List<Badge> findAllActiveBadgesOrderedByRarity();
    
    List<Badge> findByBadgeCategoryAndIsActiveTrue(Badge.BadgeCategory category);
    
    List<Badge> findByRarityLevelAndIsActiveTrue(Integer rarityLevel);
    
    List<Badge> findByBadgeTypeAndIsActiveTrue(Badge.BadgeType badgeType);
}
