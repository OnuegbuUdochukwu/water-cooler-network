package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.DepartmentDTO;
import com.codewithudo.backend.entity.Department;
import com.codewithudo.backend.entity.UserDepartment;
import com.codewithudo.backend.repository.DepartmentRepository;
import com.codewithudo.backend.repository.UserDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private UserDepartmentRepository userDepartmentRepository;
    
    public DepartmentDTO createDepartment(String name, String description, Long companyId, Long headUserId, Long parentDepartmentId) {
        // Check if department name already exists in company
        if (departmentRepository.existsByCompanyIdAndName(companyId, name)) {
            throw new RuntimeException("Department with this name already exists in the company");
        }
        
        Department department = new Department();
        department.setName(name);
        department.setDescription(description);
        department.setCompanyId(companyId);
        department.setHeadUserId(headUserId);
        department.setParentDepartmentId(parentDepartmentId);
        department.setIsActive(true);
        
        Department savedDepartment = departmentRepository.save(department);
        
        // If head user is specified, add them to the department with HEAD role
        if (headUserId != null) {
            addUserToDepartment(headUserId, savedDepartment.getId(), UserDepartment.DepartmentRole.HEAD, null);
        }
        
        return DepartmentDTO.fromEntity(savedDepartment);
    }
    
    public List<DepartmentDTO> getCompanyDepartments(Long companyId) {
        return departmentRepository.findByCompanyIdAndIsActiveTrue(companyId)
                .stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DepartmentDTO> getRootDepartments(Long companyId) {
        return departmentRepository.findRootDepartmentsByCompany(companyId)
                .stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DepartmentDTO> getSubDepartments(Long parentDepartmentId) {
        return departmentRepository.findByParentDepartmentIdAndIsActiveTrue(parentDepartmentId)
                .stream()
                .map(DepartmentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Optional<DepartmentDTO> getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .map(DepartmentDTO::fromEntity);
    }
    
    public DepartmentDTO updateDepartment(Long id, String name, String description, Long headUserId) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        // Check if new name conflicts with existing department in same company
        if (!department.getName().equals(name) && 
            departmentRepository.existsByCompanyIdAndName(department.getCompanyId(), name)) {
            throw new RuntimeException("Department with this name already exists in the company");
        }
        
        department.setName(name);
        department.setDescription(description);
        
        // Update head user if changed
        if (headUserId != null && !headUserId.equals(department.getHeadUserId())) {
            // Remove HEAD role from previous head
            if (department.getHeadUserId() != null) {
                removeUserFromDepartment(department.getHeadUserId(), id);
            }
            
            // Add HEAD role to new head
            addUserToDepartment(headUserId, id, UserDepartment.DepartmentRole.HEAD, null);
            department.setHeadUserId(headUserId);
        }
        
        Department savedDepartment = departmentRepository.save(department);
        return DepartmentDTO.fromEntity(savedDepartment);
    }
    
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        // Check if department has sub-departments
        List<Department> subDepartments = departmentRepository.findByParentDepartmentIdAndIsActiveTrue(id);
        if (!subDepartments.isEmpty()) {
            throw new RuntimeException("Cannot delete department with active sub-departments");
        }
        
        // Remove all users from department
        List<UserDepartment> userDepartments = userDepartmentRepository.findByDepartmentIdAndIsActiveTrue(id);
        for (UserDepartment userDept : userDepartments) {
            userDept.setIsActive(false);
            userDepartmentRepository.save(userDept);
        }
        
        // Deactivate department
        department.setIsActive(false);
        departmentRepository.save(department);
    }
    
    public void addUserToDepartment(Long userId, Long departmentId, UserDepartment.DepartmentRole role, String jobTitle) {
        // Check if user is already in department
        if (userDepartmentRepository.existsByUserIdAndDepartmentId(userId, departmentId)) {
            throw new RuntimeException("User is already in this department");
        }
        
        UserDepartment userDepartment = new UserDepartment();
        userDepartment.setUserId(userId);
        userDepartment.setDepartmentId(departmentId);
        userDepartment.setRole(role);
        userDepartment.setJobTitle(jobTitle);
        userDepartment.setIsActive(true);
        
        userDepartmentRepository.save(userDepartment);
    }
    
    public void removeUserFromDepartment(Long userId, Long departmentId) {
        Optional<UserDepartment> userDepartment = userDepartmentRepository
                .findByUserIdAndDepartmentIdAndIsActiveTrue(userId, departmentId);
        
        if (userDepartment.isPresent()) {
            UserDepartment ud = userDepartment.get();
            ud.setIsActive(false);
            userDepartmentRepository.save(ud);
        }
    }
    
    public List<UserDepartment> getDepartmentMembers(Long departmentId) {
        return userDepartmentRepository.findByDepartmentIdAndIsActiveTrue(departmentId);
    }
    
    public List<UserDepartment> getUserDepartments(Long userId) {
        return userDepartmentRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    public long getDepartmentMemberCount(Long departmentId) {
        return userDepartmentRepository.countActiveByDepartment(departmentId);
    }
}
