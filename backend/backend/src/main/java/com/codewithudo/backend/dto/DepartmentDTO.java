package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    
    private Long id;
    private String name;
    private String description;
    private Long companyId;
    private String companyName;
    private Long headUserId;
    private String headUserName;
    private Long parentDepartmentId;
    private String parentDepartmentName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long memberCount;
    
    public static DepartmentDTO fromEntity(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setCompanyId(department.getCompanyId());
        dto.setHeadUserId(department.getHeadUserId());
        dto.setParentDepartmentId(department.getParentDepartmentId());
        dto.setIsActive(department.getIsActive());
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUpdatedAt(department.getUpdatedAt());
        return dto;
    }
}
