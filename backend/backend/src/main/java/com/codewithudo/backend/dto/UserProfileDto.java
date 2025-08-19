package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileDto {
    
    private Long id;
    private String name;
    private String email;
    private User.UserRole role;
    private String industry;
    private String skills;
    private String interests;
    private Long companyId;
    private String linkedinUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserProfileDto fromUser(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIndustry(user.getIndustry());
        dto.setSkills(user.getSkills());
        dto.setInterests(user.getInterests());
        dto.setCompanyId(user.getCompanyId());
        dto.setLinkedinUrl(user.getLinkedinUrl());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
