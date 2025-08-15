package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.UserBadge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadgeDTO {
    private Long id;
    private Long userId;
    private BadgeDTO badge;
    private LocalDateTime earnedAt;
    private Integer currentProgress;
    private boolean isDisplayed;
    private String progressPercentage;
    
    public static UserBadgeDTO fromEntity(UserBadge userBadge) {
        UserBadgeDTO dto = new UserBadgeDTO();
        dto.setId(userBadge.getId());
        dto.setUserId(userBadge.getUserId());
        dto.setBadge(BadgeDTO.fromEntity(userBadge.getBadge()));
        dto.setEarnedAt(userBadge.getEarnedAt());
        dto.setCurrentProgress(userBadge.getCurrentProgress());
        dto.setDisplayed(userBadge.getIsDisplayed());
        
        if (userBadge.getBadge().getRequiredCount() != null && userBadge.getCurrentProgress() != null) {
            double percentage = (double) userBadge.getCurrentProgress() / userBadge.getBadge().getRequiredCount() * 100;
            dto.setProgressPercentage(String.format("%.1f%%", Math.min(percentage, 100.0)));
        } else {
            dto.setProgressPercentage("100%");
        }
        
        return dto;
    }
}
