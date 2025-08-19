package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserPreferenceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceProfileRepository extends JpaRepository<UserPreferenceProfile, Long> {
    
    Optional<UserPreferenceProfile> findByUserId(Long userId);
    
    List<UserPreferenceProfile> findByExperienceLevel(UserPreferenceProfile.ExperienceLevel experienceLevel);
    
    List<UserPreferenceProfile> findByCommunicationStyle(UserPreferenceProfile.CommunicationStyle communicationStyle);
    
    @Query("SELECT upp FROM UserPreferenceProfile upp WHERE upp.lastUpdated < :threshold")
    List<UserPreferenceProfile> findOutdatedProfiles(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT upp FROM UserPreferenceProfile upp WHERE upp.userId IN :userIds")
    List<UserPreferenceProfile> findByUserIds(@Param("userIds") List<Long> userIds);
    
    @Query("SELECT COUNT(upp) FROM UserPreferenceProfile upp WHERE upp.experienceLevel = :level")
    Long countByExperienceLevel(@Param("level") UserPreferenceProfile.ExperienceLevel level);
}
