package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.CompanyInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyInvitationRepository extends JpaRepository<CompanyInvitation, Long> {
    
    Optional<CompanyInvitation> findByInvitationToken(String invitationToken);
    
    List<CompanyInvitation> findByCompanyIdAndStatus(Long companyId, CompanyInvitation.InvitationStatus status);
    
    List<CompanyInvitation> findByEmailAndStatus(String email, CompanyInvitation.InvitationStatus status);
    
    List<CompanyInvitation> findByInvitedByUserId(Long invitedByUserId);
    
    @Query("SELECT ci FROM CompanyInvitation ci WHERE ci.expiresAt < :now AND ci.status = 'PENDING'")
    List<CompanyInvitation> findExpiredInvitations(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(ci) FROM CompanyInvitation ci WHERE ci.companyId = :companyId AND ci.status = 'PENDING'")
    long countPendingByCompany(@Param("companyId") Long companyId);
    
    boolean existsByEmailAndCompanyIdAndStatus(String email, Long companyId, CompanyInvitation.InvitationStatus status);
    
    void deleteByCompanyIdAndStatus(Long companyId, CompanyInvitation.InvitationStatus status);
}
