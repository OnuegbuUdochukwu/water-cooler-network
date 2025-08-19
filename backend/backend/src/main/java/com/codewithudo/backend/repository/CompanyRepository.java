package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    Optional<Company> findByName(String name);
    
    Optional<Company> findByNameAndIsActiveTrue(String name);
    
    List<Company> findByIsActiveTrue();
    
    List<Company> findBySubscriptionTier(Company.SubscriptionTier subscriptionTier);
    
    boolean existsByName(String name);
}
