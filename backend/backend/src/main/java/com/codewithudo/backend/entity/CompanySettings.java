package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false, unique = true)
    private Long companyId;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "primary_color")
    private String primaryColor = "#007bff";
    
    @Column(name = "secondary_color")
    private String secondaryColor = "#6c757d";
    
    @Column(name = "allowed_domains", columnDefinition = "TEXT")
    private String allowedDomains;
    
    @Column(name = "require_domain_verification", nullable = false)
    private Boolean requireDomainVerification = true;
    
    @Column(name = "allow_external_matching", nullable = false)
    private Boolean allowExternalMatching = false;
    
    @Column(name = "max_employees")
    private Integer maxEmployees;
    
    @Column(name = "enable_analytics", nullable = false)
    private Boolean enableAnalytics = true;
    
    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
