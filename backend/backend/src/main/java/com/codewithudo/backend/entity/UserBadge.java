package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "badge_id", nullable = false)
    private Long badgeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", insertable = false, updatable = false)
    private Badge badge;
    
    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;
    
    @Column(name = "progress_data", columnDefinition = "TEXT")
    private String progressData;
    
    @Column(name = "current_progress")
    private Integer currentProgress = 0;
    
    @Column(name = "is_displayed", nullable = false)
    private Boolean isDisplayed = true;
    
    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;
}
