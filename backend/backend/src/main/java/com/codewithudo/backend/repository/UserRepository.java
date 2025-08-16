package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    List<User> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    List<User> findByCompanyId(Long companyId);
    
    List<User> findByIndustryAndIsActiveTrue(String industry);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.id != :userId")
    List<User> findAllActiveUsersExcept(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role = 'USER'")
    List<User> findAllActiveRegularUsers();
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.id NOT IN :excludedIds")
    List<User> findActiveUsersExcluding(@Param("excludedIds") List<Long> excludedIds);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIsActiveTrue(String email);
}
