package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    List<ActivityLog> findByUserId(Long userId);
    
    List<ActivityLog> findByUserIdAndActivityType(Long userId, ActivityLog.ActivityType activityType);
    
    @Query("SELECT al FROM ActivityLog al WHERE al.userId = :userId AND al.createdAt >= :startDate")
    List<ActivityLog> findByUserIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT al FROM ActivityLog al WHERE al.userId = :userId AND al.activityType = :activityType AND al.createdAt >= :startDate")
    List<ActivityLog> findByUserIdAndActivityTypeAndCreatedAtAfter(
        @Param("userId") Long userId, 
        @Param("activityType") ActivityLog.ActivityType activityType, 
        @Param("startDate") LocalDateTime startDate
    );
    
    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.userId = :userId AND al.activityType = :activityType")
    Long countByUserIdAndActivityType(@Param("userId") Long userId, @Param("activityType") ActivityLog.ActivityType activityType);
    
    @Query("SELECT SUM(al.pointsEarned) FROM ActivityLog al WHERE al.userId = :userId")
    Long getTotalPointsByUserId(@Param("userId") Long userId);
}
