package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.CompanyAnnouncement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAnnouncementDTO {
    
    private Long id;
    private Long companyId;
    private String companyName;
    private Long authorUserId;
    private String authorUserName;
    private String title;
    private String content;
    private CompanyAnnouncement.AnnouncementType type;
    private CompanyAnnouncement.Priority priority;
    private String targetDepartments;
    private Boolean isPinned;
    private Boolean isActive;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static CompanyAnnouncementDTO fromEntity(CompanyAnnouncement announcement) {
        CompanyAnnouncementDTO dto = new CompanyAnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setCompanyId(announcement.getCompanyId());
        dto.setAuthorUserId(announcement.getAuthorUserId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setType(announcement.getType());
        dto.setPriority(announcement.getPriority());
        dto.setTargetDepartments(announcement.getTargetDepartments());
        dto.setIsPinned(announcement.getIsPinned());
        dto.setIsActive(announcement.getIsActive());
        dto.setPublishedAt(announcement.getPublishedAt());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());
        return dto;
    }
}
