package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Lounge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoungeRepository extends JpaRepository<Lounge, Long> {
    
    List<Lounge> findByIsActiveTrueOrderByLastActivityDesc();
    
    List<Lounge> findByTopicContainingIgnoreCaseAndIsActiveTrue(String topic);
    
    List<Lounge> findByCategoryContainingIgnoreCaseAndIsActiveTrue(String category);
    
    List<Lounge> findByTagsContainingIgnoreCaseAndIsActiveTrue(String tag);
    
    List<Lounge> findByCreatedByAndIsActiveTrue(Long createdBy);
    
    List<Lounge> findByVisibilityAndIsActiveTrue(Lounge.Visibility visibility);
    
    List<Lounge> findByIsFeaturedTrueAndIsActiveTrue();
    
    @Query("SELECT l FROM Lounge l WHERE l.isActive = true AND (l.title LIKE %:searchTerm% OR l.description LIKE %:searchTerm% OR l.topic LIKE %:searchTerm%)")
    List<Lounge> searchLounges(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT l FROM Lounge l WHERE l.isActive = true AND l.lastActivity > :since ORDER BY l.lastActivity DESC")
    List<Lounge> findActiveLoungesSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT l FROM Lounge l WHERE l.isActive = true AND l.currentParticipants < l.maxParticipants ORDER BY l.currentParticipants ASC")
    List<Lounge> findLoungesWithAvailableSpace();
    
    Optional<Lounge> findByIdAndIsActiveTrue(Long id);
    
    boolean existsByTitleAndIsActiveTrue(String title);
}
