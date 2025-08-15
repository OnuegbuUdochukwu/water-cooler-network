package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.CompanyDTO;
import com.codewithudo.backend.entity.Company;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;
    
    @PostMapping
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CreateCompanyRequest request, Authentication auth) {
        // Note: In a real implementation, you'd get the user ID from the authentication
        // For now, we'll assume the request contains the admin ID
        CompanyDTO company = companyService.createCompany(request.getName(), request.getAdminId());
        return ResponseEntity.ok(company);
    }
    
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllActiveCompanies();
        return ResponseEntity.ok(companies);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<CompanyDTO> getCompanyByName(@PathVariable String name) {
        return companyService.getCompanyByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @companyService.isUserCompanyAdmin(authentication.principal.id, #id)")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyDTO company = companyService.updateCompany(id, request.getName(), request.getSubscriptionTier());
        return ResponseEntity.ok(company);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @companyService.isUserCompanyAdmin(authentication.principal.id, #id)")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or @companyService.isUserCompanyAdmin(authentication.principal.id, #id)")
    public ResponseEntity<Void> deactivateCompany(@PathVariable Long id) {
        companyService.deactivateCompany(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/employees")
    @PreAuthorize("@companyService.isUserCompanyAdmin(authentication.principal.id, #id)")
    public ResponseEntity<List<User>> getCompanyEmployees(@PathVariable Long id) {
        List<User> employees = companyService.getCompanyEmployees(id);
        return ResponseEntity.ok(employees);
    }
    
    // Request DTOs
    public static class CreateCompanyRequest {
        private String name;
        private Long adminId;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
    }
    
    public static class UpdateCompanyRequest {
        private String name;
        private Company.SubscriptionTier subscriptionTier;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Company.SubscriptionTier getSubscriptionTier() { return subscriptionTier; }
        public void setSubscriptionTier(Company.SubscriptionTier subscriptionTier) { this.subscriptionTier = subscriptionTier; }
    }
}
