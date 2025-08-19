package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.CompanyAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompanyAnnouncementRepository extends JpaRepository<CompanyAnnouncement, Long> {
    
    List<CompanyAnnouncement> findByCompanyIdAndIsActiveTrueOrderByCreatedAtDesc(Long companyId);
    
    List<CompanyAnnouncement> findByCompanyIdAndIsPinnedTrueAndIsActiveTrueOrderByCreatedAtDesc(Long companyId);
    
    List<CompanyAnnouncement> findByCompanyIdAndTypeAndIsActiveTrueOrderByCreatedAtDesc(Long companyId, CompanyAnnouncement.AnnouncementType type);
    
    List<CompanyAnnouncement> findByCompanyIdAndPriorityAndIsActiveTrueOrderByCreatedAtDesc(Long companyId, CompanyAnnouncement.Priority priority);
    
    List<CompanyAnnouncement> findByAuthorUserId(Long authorUserId);
    
    @Query("SELECT ca FROM CompanyAnnouncement ca WHERE ca.companyId = :companyId AND ca.publishedAt <= :now AND ca.isActive = true ORDER BY ca.isPinned DESC, ca.createdAt DESC")
    List<CompanyAnnouncement> findPublishedByCompany(@Param("companyId") Long companyId, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(ca) FROM CompanyAnnouncement ca WHERE ca.companyId = :companyId AND ca.isActive = true")
    long countActiveByCompany(@Param("companyId") Long companyId);
}
