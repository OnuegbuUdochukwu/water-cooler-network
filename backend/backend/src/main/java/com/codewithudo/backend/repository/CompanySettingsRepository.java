package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {
    
    Optional<CompanySettings> findByCompanyId(Long companyId);
    
    boolean existsByCompanyId(Long companyId);
    
    void deleteByCompanyId(Long companyId);
}
