package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.UserStreak;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStreakDTO {
    private Long id;
    private Long userId;
    private UserStreak.StreakType streakType;
    private Integer currentCount;
    private Integer bestCount;
    private LocalDate lastActivityDate;
    private boolean isActive;
    private String streakTypeDisplay;
    
    public static UserStreakDTO fromEntity(UserStreak userStreak) {
        UserStreakDTO dto = new UserStreakDTO();
        dto.setId(userStreak.getId());
        dto.setUserId(userStreak.getUserId());
        dto.setStreakType(userStreak.getStreakType());
        dto.setCurrentCount(userStreak.getCurrentCount());
        dto.setBestCount(userStreak.getBestCount());
        dto.setLastActivityDate(userStreak.getLastActivityDate());
        dto.setActive(userStreak.isStreakActive());
        dto.setStreakTypeDisplay(getStreakTypeDisplay(userStreak.getStreakType()));
        return dto;
    }
    
    private static String getStreakTypeDisplay(UserStreak.StreakType streakType) {
        return switch (streakType) {
            case DAILY_LOGIN -> "Daily Login";
            case COFFEE_CHAT -> "Coffee Chat";
            case LOUNGE_PARTICIPATION -> "Lounge Participation";
            case MESSAGE_STREAK -> "Message Streak";
        };
    }
}
