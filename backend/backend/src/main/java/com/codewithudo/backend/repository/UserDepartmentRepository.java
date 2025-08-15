package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.UserDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {
    
    List<UserDepartment> findByUserIdAndIsActiveTrue(Long userId);
    
    List<UserDepartment> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    
    Optional<UserDepartment> findByUserIdAndDepartmentIdAndIsActiveTrue(Long userId, Long departmentId);
    
    List<UserDepartment> findByDepartmentIdAndRole(Long departmentId, UserDepartment.DepartmentRole role);
    
    @Query("SELECT ud FROM UserDepartment ud JOIN Department d ON ud.departmentId = d.id WHERE d.companyId = :companyId AND ud.isActive = true")
    List<UserDepartment> findByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(ud) FROM UserDepartment ud WHERE ud.departmentId = :departmentId AND ud.isActive = true")
    long countActiveByDepartment(@Param("departmentId") Long departmentId);
    
    boolean existsByUserIdAndDepartmentId(Long userId, Long departmentId);
}
