package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.DepartmentDTO;
import com.codewithudo.backend.entity.UserDepartment;
import com.codewithudo.backend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDTO department = departmentService.createDepartment(
                request.getName(), 
                request.getDescription(), 
                request.getCompanyId(), 
                request.getHeadUserId(), 
                request.getParentDepartmentId()
        );
        return ResponseEntity.ok(department);
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<DepartmentDTO>> getCompanyDepartments(@PathVariable Long companyId) {
        List<DepartmentDTO> departments = departmentService.getCompanyDepartments(companyId);
        return ResponseEntity.ok(departments);
    }
    
    @GetMapping("/company/{companyId}/root")
    public ResponseEntity<List<DepartmentDTO>> getRootDepartments(@PathVariable Long companyId) {
        List<DepartmentDTO> departments = departmentService.getRootDepartments(companyId);
        return ResponseEntity.ok(departments);
    }
    
    @GetMapping("/{id}/subdepartments")
    public ResponseEntity<List<DepartmentDTO>> getSubDepartments(@PathVariable Long id) {
        List<DepartmentDTO> departments = departmentService.getSubDepartments(id);
        return ResponseEntity.ok(departments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentRequest request) {
        DepartmentDTO department = departmentService.updateDepartment(id, request.getName(), request.getDescription(), request.getHeadUserId());
        return ResponseEntity.ok(department);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> addUserToDepartment(@PathVariable Long id, @Valid @RequestBody AddUserRequest request) {
        departmentService.addUserToDepartment(request.getUserId(), id, request.getRole(), request.getJobTitle());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeUserFromDepartment(@PathVariable Long id, @PathVariable Long userId) {
        departmentService.removeUserFromDepartment(userId, id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDepartment>> getDepartmentMembers(@PathVariable Long id) {
        List<UserDepartment> members = departmentService.getDepartmentMembers(id);
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserDepartment>> getUserDepartments(@PathVariable Long userId) {
        List<UserDepartment> departments = departmentService.getUserDepartments(userId);
        return ResponseEntity.ok(departments);
    }
    
    // Request DTOs
    public static class CreateDepartmentRequest {
        private String name;
        private String description;
        private Long companyId;
        private Long headUserId;
        private Long parentDepartmentId;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public Long getHeadUserId() { return headUserId; }
        public void setHeadUserId(Long headUserId) { this.headUserId = headUserId; }
        public Long getParentDepartmentId() { return parentDepartmentId; }
        public void setParentDepartmentId(Long parentDepartmentId) { this.parentDepartmentId = parentDepartmentId; }
    }
    
    public static class UpdateDepartmentRequest {
        private String name;
        private String description;
        private Long headUserId;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getHeadUserId() { return headUserId; }
        public void setHeadUserId(Long headUserId) { this.headUserId = headUserId; }
    }
    
    public static class AddUserRequest {
        private Long userId;
        private UserDepartment.DepartmentRole role;
        private String jobTitle;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public UserDepartment.DepartmentRole getRole() { return role; }
        public void setRole(UserDepartment.DepartmentRole role) { this.role = role; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    }
}
