package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    List<Department> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    List<Department> findByCompanyId(Long companyId);
    
    Optional<Department> findByCompanyIdAndName(Long companyId, String name);
    
    List<Department> findByParentDepartmentIdAndIsActiveTrue(Long parentDepartmentId);
    
    List<Department> findByHeadUserId(Long headUserId);
    
    @Query("SELECT d FROM Department d WHERE d.companyId = :companyId AND d.parentDepartmentId IS NULL AND d.isActive = true")
    List<Department> findRootDepartmentsByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(d) FROM Department d WHERE d.companyId = :companyId AND d.isActive = true")
    long countActiveByCompany(@Param("companyId") Long companyId);
    
    boolean existsByCompanyIdAndName(Long companyId, String name);
}
