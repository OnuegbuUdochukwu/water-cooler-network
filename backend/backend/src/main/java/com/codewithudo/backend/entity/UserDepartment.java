package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDepartment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "department_id", nullable = false)
    private Long departmentId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentRole role = DepartmentRole.MEMBER;
    
    @Column(name = "job_title")
    private String jobTitle;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum DepartmentRole {
        MEMBER, LEAD, MANAGER, HEAD
    }
}
