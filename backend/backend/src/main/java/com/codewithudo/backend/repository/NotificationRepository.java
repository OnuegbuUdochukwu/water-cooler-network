package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Notification;
import com.codewithudo.backend.entity.Notification.NotificationType;
import com.codewithudo.backend.entity.Notification.NotificationPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by user ID
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find unread notifications
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // Count unread notifications
    Long countByUserIdAndIsReadFalse(Long userId);
    
    // Find notifications by type
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);
    
    // Find notifications by priority
    List<Notification> findByUserIdAndPriorityOrderByCreatedAtDesc(Long userId, NotificationPriority priority);
    
    // Find recent notifications (last 24 hours)
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    // Mark notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :id AND n.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
    
    // Mark all notifications as read for user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
    
    // Delete expired notifications
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    int deleteExpiredNotifications(@Param("now") LocalDateTime now);
    
    // Find notifications by date range
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    // Find high priority unread notifications
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.priority IN ('HIGH', 'URGENT') ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findHighPriorityUnreadNotifications(@Param("userId") Long userId);
}
