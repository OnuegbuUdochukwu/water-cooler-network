package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.CompanyInvitation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInvitationDTO {
    
    private Long id;
    private Long companyId;
    private String companyName;
    private String email;
    private Long invitedByUserId;
    private String invitedByUserName;
    private Long departmentId;
    private String departmentName;
    private CompanyInvitation.InvitationStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;
    
    public static CompanyInvitationDTO fromEntity(CompanyInvitation invitation) {
        CompanyInvitationDTO dto = new CompanyInvitationDTO();
        dto.setId(invitation.getId());
        dto.setCompanyId(invitation.getCompanyId());
        dto.setEmail(invitation.getEmail());
        dto.setInvitedByUserId(invitation.getInvitedByUserId());
        dto.setDepartmentId(invitation.getDepartmentId());
        dto.setStatus(invitation.getStatus());
        dto.setExpiresAt(invitation.getExpiresAt());
        dto.setAcceptedAt(invitation.getAcceptedAt());
        dto.setCreatedAt(invitation.getCreatedAt());
        return dto;
    }
}
