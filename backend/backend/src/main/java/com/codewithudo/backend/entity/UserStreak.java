package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_streaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStreak {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "streak_type", nullable = false)
    private StreakType streakType;
    
    @Column(name = "current_count", nullable = false)
    private Integer currentCount = 0;
    
    @Column(name = "best_count", nullable = false)
    private Integer bestCount = 0;
    
    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum StreakType {
        DAILY_LOGIN,
        COFFEE_CHAT,
        LOUNGE_PARTICIPATION,
        MESSAGE_STREAK
    }
    
    public void incrementStreak() {
        this.currentCount++;
        if (this.currentCount > this.bestCount) {
            this.bestCount = this.currentCount;
        }
        this.lastActivityDate = LocalDate.now();
    }
    
    public void resetStreak() {
        this.currentCount = 0;
        this.lastActivityDate = LocalDate.now();
    }
    
    public boolean isStreakActive() {
        if (lastActivityDate == null) return false;
        return !lastActivityDate.isBefore(LocalDate.now().minusDays(1));
    }
}
