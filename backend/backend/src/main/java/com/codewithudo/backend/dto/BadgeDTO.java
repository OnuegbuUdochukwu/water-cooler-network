package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Badge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Badge.BadgeType badgeType;
    private Badge.BadgeCategory badgeCategory;
    private Integer requiredCount;
    private Integer rarityLevel;
    private String rarityDisplay;
    private LocalDateTime createdAt;
    
    public static BadgeDTO fromEntity(Badge badge) {
        BadgeDTO dto = new BadgeDTO();
        dto.setId(badge.getId());
        dto.setName(badge.getName());
        dto.setDescription(badge.getDescription());
        dto.setIconUrl(badge.getIconUrl());
        dto.setBadgeType(badge.getBadgeType());
        dto.setBadgeCategory(badge.getBadgeCategory());
        dto.setRequiredCount(badge.getRequiredCount());
        dto.setRarityLevel(badge.getRarityLevel());
        dto.setRarityDisplay(getRarityDisplay(badge.getRarityLevel()));
        dto.setCreatedAt(badge.getCreatedAt());
        return dto;
    }
    
    private static String getRarityDisplay(Integer rarityLevel) {
        return switch (rarityLevel) {
            case 1 -> "Common";
            case 2 -> "Rare";
            case 3 -> "Epic";
            case 4 -> "Legendary";
            default -> "Common";
        };
    }
}
