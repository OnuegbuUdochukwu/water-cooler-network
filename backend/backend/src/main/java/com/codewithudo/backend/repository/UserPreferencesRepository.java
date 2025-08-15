package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    
    Optional<UserPreferences> findByUserId(Long userId);
    
    List<UserPreferences> findByIsAvailableForMatchingTrue();
    
    @Query("SELECT up FROM UserPreferences up WHERE up.isAvailableForMatching = true AND up.userId != :userId")
    List<UserPreferences> findAvailableUsersForMatching(@Param("userId") Long userId);
    
    @Query("SELECT up FROM UserPreferences up WHERE up.isAvailableForMatching = true AND up.preferredIndustries LIKE %:industry%")
    List<UserPreferences> findByPreferredIndustry(@Param("industry") String industry);
    
    @Query("SELECT up FROM UserPreferences up WHERE up.isAvailableForMatching = true AND up.preferredExperienceLevel = :level")
    List<UserPreferences> findByPreferredExperienceLevel(@Param("level") UserPreferences.ExperienceLevel level);
    
    boolean existsByUserId(Long userId);
}
