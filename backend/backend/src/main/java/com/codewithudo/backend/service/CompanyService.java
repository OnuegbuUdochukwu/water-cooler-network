package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.CompanyDTO;
import com.codewithudo.backend.entity.Company;
import com.codewithudo.backend.entity.CompanySettings;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.repository.CompanyRepository;
import com.codewithudo.backend.repository.CompanySettingsRepository;
import com.codewithudo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CompanySettingsRepository companySettingsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public CompanyDTO createCompany(String name, Long adminId) {
        // Validate admin user exists and has appropriate role
        Optional<User> adminUser = userRepository.findById(adminId);
        if (adminUser.isEmpty()) {
            throw new RuntimeException("Admin user not found");
        }
        
        if (adminUser.get().getRole() != User.UserRole.CORPORATE_ADMIN && 
            adminUser.get().getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("User does not have corporate admin privileges");
        }
        
        // Check if company name already exists
        if (companyRepository.existsByName(name)) {
            throw new RuntimeException("Company with this name already exists");
        }
        
        // Create company
        Company company = new Company();
        company.setName(name);
        company.setAdminId(adminId);
        company.setSubscriptionTier(Company.SubscriptionTier.FREE);
        company.setIsActive(true);
        
        Company savedCompany = companyRepository.save(company);
        
        // Create default company settings
        createDefaultCompanySettings(savedCompany.getId());
        
        // Update user's company association
        User admin = adminUser.get();
        admin.setCompanyId(savedCompany.getId());
        userRepository.save(admin);
        
        return CompanyDTO.fromEntity(savedCompany);
    }
    
    public List<CompanyDTO> getAllActiveCompanies() {
        return companyRepository.findByIsActiveTrue()
                .stream()
                .map(CompanyDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Optional<CompanyDTO> getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(CompanyDTO::fromEntity);
    }
    
    public Optional<CompanyDTO> getCompanyByName(String name) {
        return companyRepository.findByNameAndIsActiveTrue(name)
                .map(CompanyDTO::fromEntity);
    }
    
    public CompanyDTO updateCompany(Long id, String name, Company.SubscriptionTier subscriptionTier) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        // Check if new name conflicts with existing company
        if (!company.getName().equals(name) && companyRepository.existsByName(name)) {
            throw new RuntimeException("Company with this name already exists");
        }
        
        company.setName(name);
        if (subscriptionTier != null) {
            company.setSubscriptionTier(subscriptionTier);
        }
        
        Company savedCompany = companyRepository.save(company);
        return CompanyDTO.fromEntity(savedCompany);
    }
    
    public void deactivateCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        company.setIsActive(false);
        companyRepository.save(company);
    }
    
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        // Remove company association from all users
        List<User> companyUsers = userRepository.findByCompanyId(id);
        for (User user : companyUsers) {
            user.setCompanyId(null);
            userRepository.save(user);
        }
        
        // Delete company settings
        companySettingsRepository.deleteByCompanyId(id);
        
        // Delete company
        companyRepository.delete(company);
    }
    
    public boolean isUserCompanyAdmin(Long userId, Long companyId) {
        Optional<Company> company = companyRepository.findById(companyId);
        return company.isPresent() && company.get().getAdminId().equals(userId);
    }
    
    public List<User> getCompanyEmployees(Long companyId) {
        return userRepository.findByCompanyId(companyId);
    }
    
    private void createDefaultCompanySettings(Long companyId) {
        CompanySettings settings = new CompanySettings();
        settings.setCompanyId(companyId);
        settings.setPrimaryColor("#007bff");
        settings.setSecondaryColor("#6c757d");
        settings.setRequireDomainVerification(true);
        settings.setAllowExternalMatching(false);
        settings.setEnableAnalytics(true);
        
        companySettingsRepository.save(settings);
    }
}
