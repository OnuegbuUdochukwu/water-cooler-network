package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    
    /**
     * Find notification preferences by user ID
     */
    Optional<NotificationPreferences> findByUserId(Long userId);
    
    /**
     * Check if notification preferences exist for a user
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Delete notification preferences by user ID
     */
    void deleteByUserId(Long userId);
}
