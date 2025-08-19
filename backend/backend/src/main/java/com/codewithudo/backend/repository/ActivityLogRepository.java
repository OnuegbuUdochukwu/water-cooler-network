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
    
    // Leaderboard queries
    @Query(value = """
        SELECT u.id, u.name, u.email, 
               COALESCE(SUM(al.points_earned), 0) as total_points,
               COALESCE(ub.badge_count, 0) as total_badges,
               COALESCE(us.longest_streak, 0) as longest_streak,
               COALESCE(us.streak_type, 'None') as streak_type,
               ROW_NUMBER() OVER (ORDER BY COALESCE(SUM(al.points_earned), 0) DESC) as rank
        FROM users u
        LEFT JOIN activity_log al ON u.id = al.user_id
        LEFT JOIN (
            SELECT user_id, COUNT(*) as badge_count 
            FROM user_badges 
            GROUP BY user_id
        ) ub ON u.id = ub.user_id
        LEFT JOIN (
            SELECT user_id, MAX(best_count) as longest_streak, streak_type
            FROM user_streaks
            GROUP BY user_id, streak_type
        ) us ON u.id = us.user_id
        WHERE u.is_active = true
        GROUP BY u.id, u.name, u.email, ub.badge_count, us.longest_streak, us.streak_type
        ORDER BY total_points DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getLeaderboardData(@Param("limit") int limit);
    
    @Query(value = """
        SELECT COUNT(*) + 1
        FROM (
            SELECT u.id, COALESCE(SUM(al.points_earned), 0) as total_points
            FROM users u
            LEFT JOIN activity_log al ON u.id = al.user_id
            WHERE u.is_active = true
            GROUP BY u.id
            HAVING COALESCE(SUM(al.points_earned), 0) > (
                SELECT COALESCE(SUM(al2.points_earned), 0)
                FROM users u2
                LEFT JOIN activity_log al2 ON u2.id = al2.user_id
                WHERE u2.id = :userId
                GROUP BY u2.id
            )
        ) ranked_users
        """, nativeQuery = true)
    Integer getUserRank(@Param("userId") Long userId);
    
    @Query(value = """
        SELECT u.id, u.name, 
               COALESCE(SUM(al.points_earned), 0) as total_points,
               ROW_NUMBER() OVER (ORDER BY COALESCE(SUM(al.points_earned), 0) DESC) as rank
        FROM users u
        LEFT JOIN activity_log al ON u.id = al.user_id
        WHERE u.is_active = true AND al.activity_type = :category
        GROUP BY u.id, u.name
        ORDER BY total_points DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> getTopPerformersByCategory(@Param("category") String category, @Param("limit") int limit);
}
