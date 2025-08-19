package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.CompanyInvitationDTO;
import com.codewithudo.backend.entity.CompanyInvitation;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.repository.CompanyInvitationRepository;
import com.codewithudo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyInvitationService {
    
    @Autowired
    private CompanyInvitationRepository invitationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public CompanyInvitationDTO createInvitation(Long companyId, String email, Long invitedByUserId, Long departmentId) {
        // Check if user already has pending invitation for this company
        if (invitationRepository.existsByEmailAndCompanyIdAndStatus(email, companyId, CompanyInvitation.InvitationStatus.PENDING)) {
            throw new RuntimeException("User already has a pending invitation for this company");
        }
        
        // Check if user is already part of the company
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && companyId.equals(existingUser.get().getCompanyId())) {
            throw new RuntimeException("User is already part of this company");
        }
        
        CompanyInvitation invitation = new CompanyInvitation();
        invitation.setCompanyId(companyId);
        invitation.setEmail(email);
        invitation.setInvitedByUserId(invitedByUserId);
        invitation.setDepartmentId(departmentId);
        invitation.setStatus(CompanyInvitation.InvitationStatus.PENDING);
        invitation.setInvitationToken(UUID.randomUUID().toString());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days expiry
        
        CompanyInvitation savedInvitation = invitationRepository.save(invitation);
        return CompanyInvitationDTO.fromEntity(savedInvitation);
    }
    
    public List<CompanyInvitationDTO> getCompanyInvitations(Long companyId, CompanyInvitation.InvitationStatus status) {
        List<CompanyInvitation> invitations;
        if (status != null) {
            invitations = invitationRepository.findByCompanyIdAndStatus(companyId, status);
        } else {
            invitations = invitationRepository.findAll().stream()
                    .filter(inv -> inv.getCompanyId().equals(companyId))
                    .collect(Collectors.toList());
        }
        
        return invitations.stream()
                .map(CompanyInvitationDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<CompanyInvitationDTO> getUserInvitations(String email, CompanyInvitation.InvitationStatus status) {
        return invitationRepository.findByEmailAndStatus(email, status)
                .stream()
                .map(CompanyInvitationDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public CompanyInvitationDTO acceptInvitation(String invitationToken, Long userId) {
        CompanyInvitation invitation = invitationRepository.findByInvitationToken(invitationToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
        
        // Check if invitation is still valid
        if (invitation.getStatus() != CompanyInvitation.InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation is no longer valid");
        }
        
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(CompanyInvitation.InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new RuntimeException("Invitation has expired");
        }
        
        // Update user's company association
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify email matches
        if (!user.getEmail().equals(invitation.getEmail())) {
            throw new RuntimeException("Email mismatch");
        }
        
        user.setCompanyId(invitation.getCompanyId());
        userRepository.save(user);
        
        // Update invitation status
        invitation.setStatus(CompanyInvitation.InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        CompanyInvitation savedInvitation = invitationRepository.save(invitation);
        
        return CompanyInvitationDTO.fromEntity(savedInvitation);
    }
    
    public void cancelInvitation(Long invitationId) {
        CompanyInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
        
        if (invitation.getStatus() != CompanyInvitation.InvitationStatus.PENDING) {
            throw new RuntimeException("Can only cancel pending invitations");
        }
        
        invitation.setStatus(CompanyInvitation.InvitationStatus.CANCELLED);
        invitationRepository.save(invitation);
    }
    
    public void expireOldInvitations() {
        List<CompanyInvitation> expiredInvitations = invitationRepository.findExpiredInvitations(LocalDateTime.now());
        
        for (CompanyInvitation invitation : expiredInvitations) {
            invitation.setStatus(CompanyInvitation.InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
        }
    }
    
    public Optional<CompanyInvitationDTO> getInvitationByToken(String token) {
        return invitationRepository.findByInvitationToken(token)
                .map(CompanyInvitationDTO::fromEntity);
    }
    
    public long getPendingInvitationCount(Long companyId) {
        return invitationRepository.countPendingByCompany(companyId);
    }
}
