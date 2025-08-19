package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    
    private Long id;
    private String name;
    private Long adminId;
    private String adminName;
    private Company.SubscriptionTier subscriptionTier;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long employeeCount;
    private Long departmentCount;
    
    public static CompanyDTO fromEntity(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAdminId(company.getAdminId());
        dto.setSubscriptionTier(company.getSubscriptionTier());
        dto.setIsActive(company.getIsActive());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }
}
