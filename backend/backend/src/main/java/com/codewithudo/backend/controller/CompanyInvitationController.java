package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.CompanyInvitationDTO;
import com.codewithudo.backend.entity.CompanyInvitation;
import com.codewithudo.backend.service.CompanyInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/company-invitations")
public class CompanyInvitationController {
    
    @Autowired
    private CompanyInvitationService invitationService;
    
    @PostMapping
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CompanyInvitationDTO> createInvitation(@Valid @RequestBody CreateInvitationRequest request) {
        CompanyInvitationDTO invitation = invitationService.createInvitation(
                request.getCompanyId(), 
                request.getEmail(), 
                request.getInvitedByUserId(), 
                request.getDepartmentId()
        );
        return ResponseEntity.ok(invitation);
    }
    
    @GetMapping("/company/{companyId}")
    @PreAuthorize("@companyService.isUserCompanyAdmin(authentication.principal.id, #companyId)")
    public ResponseEntity<List<CompanyInvitationDTO>> getCompanyInvitations(
            @PathVariable Long companyId,
            @RequestParam(required = false) CompanyInvitation.InvitationStatus status) {
        List<CompanyInvitationDTO> invitations = invitationService.getCompanyInvitations(companyId, status);
        return ResponseEntity.ok(invitations);
    }
    
    @GetMapping("/user/{email}")
    public ResponseEntity<List<CompanyInvitationDTO>> getUserInvitations(
            @PathVariable String email,
            @RequestParam(defaultValue = "PENDING") CompanyInvitation.InvitationStatus status) {
        List<CompanyInvitationDTO> invitations = invitationService.getUserInvitations(email, status);
        return ResponseEntity.ok(invitations);
    }
    
    @GetMapping("/token/{token}")
    public ResponseEntity<CompanyInvitationDTO> getInvitationByToken(@PathVariable String token) {
        return invitationService.getInvitationByToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/accept/{token}")
    public ResponseEntity<CompanyInvitationDTO> acceptInvitation(@PathVariable String token, @Valid @RequestBody AcceptInvitationRequest request) {
        CompanyInvitationDTO invitation = invitationService.acceptInvitation(token, request.getUserId());
        return ResponseEntity.ok(invitation);
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelInvitation(@PathVariable Long id) {
        invitationService.cancelInvitation(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/company/{companyId}/pending-count")
    @PreAuthorize("@companyService.isUserCompanyAdmin(authentication.principal.id, #companyId)")
    public ResponseEntity<Long> getPendingInvitationCount(@PathVariable Long companyId) {
        long count = invitationService.getPendingInvitationCount(companyId);
        return ResponseEntity.ok(count);
    }
    
    // Request DTOs
    public static class CreateInvitationRequest {
        private Long companyId;
        private String email;
        private Long invitedByUserId;
        private Long departmentId;
        
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Long getInvitedByUserId() { return invitedByUserId; }
        public void setInvitedByUserId(Long invitedByUserId) { this.invitedByUserId = invitedByUserId; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    }
    
    public static class AcceptInvitationRequest {
        private Long userId;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
}
