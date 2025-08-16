package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    
    List<UserInteraction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<UserInteraction> findByUserIdAndInteractionTypeOrderByCreatedAtDesc(Long userId, UserInteraction.InteractionType interactionType);
    
    List<UserInteraction> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT ui FROM UserInteraction ui WHERE ui.userId = :userId AND ui.createdAt >= :since")
    List<UserInteraction> findRecentInteractions(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT ui.interactionValue, COUNT(ui) as count FROM UserInteraction ui " +
           "WHERE ui.userId = :userId AND ui.interactionType = :type " +
           "GROUP BY ui.interactionValue ORDER BY count DESC")
    List<Object[]> findTopInteractionValues(@Param("userId") Long userId, 
                                          @Param("type") UserInteraction.InteractionType type);
    
    @Query("SELECT ui FROM UserInteraction ui WHERE ui.userId = :userId AND ui.targetUserId = :targetUserId")
    List<UserInteraction> findInteractionsBetweenUsers(@Param("userId") Long userId, 
                                                      @Param("targetUserId") Long targetUserId);
    
    @Query("SELECT COUNT(ui) FROM UserInteraction ui WHERE ui.userId = :userId AND ui.interactionType = :type AND ui.createdAt >= :since")
    Long countInteractionsSince(@Param("userId") Long userId, 
                               @Param("type") UserInteraction.InteractionType type, 
                               @Param("since") LocalDateTime since);
}
