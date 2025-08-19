package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    List<User> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    @Query("SELECT u FROM User u WHERE u.companyId = :companyId")
    List<User> findByCompanyId(@Param("companyId") Long companyId);
    
    List<User> findByIsActiveTrue();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    List<User> findByIndustryAndIsActiveTrue(String industry);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role = 'USER'")
    List<User> findAllActiveRegularUsers();
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();
    
    // Advanced search methods
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
    
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    List<User> findTop5ByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(LOWER(u.name) LIKE LOWER(:query) OR LOWER(u.email) LIKE LOWER(:query)) AND " +
           "(:industry = '%' OR LOWER(u.industry) LIKE LOWER(:industry)) AND " +
           "(:skills = '%' OR LOWER(u.skills) LIKE LOWER(:skills))")
    Page<User> findUsersWithFilters(@Param("query") String query,
                                   @Param("industry") String industry,
                                   @Param("skills") String skills,
                                   @Param("location") String location,
                                   Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.id != :userId")
    List<User> findAllActiveUsersExcept(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.id NOT IN :excludedIds")
    List<User> findActiveUsersExcluding(@Param("excludedIds") List<Long> excludedIds);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIsActiveTrue(String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.companyId = :companyId AND u.lastActiveDate BETWEEN :startDate AND :endDate")
    Long countByCompanyIdAndLastActiveDateBetween(@Param("companyId") Long companyId, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
}
