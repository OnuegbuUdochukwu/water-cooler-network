package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.AnalyticsResponseDTO;
import com.codewithudo.backend.entity.AnalyticsData;
import com.codewithudo.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<Map<String, Object>> getCompanyOverview(@PathVariable Long companyId) {
        try {
            Map<String, Object> overview = analyticsService.getCompanyOverview(companyId);
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/company/{companyId}/metric/{metricType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<AnalyticsResponseDTO> getCompanyMetric(
            @PathVariable Long companyId,
            @PathVariable AnalyticsData.MetricType metricType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") AnalyticsData.PeriodType periodType) {
        
        try {
            AnalyticsResponseDTO analytics = analyticsService.getAnalytics(
                    companyId, null, metricType, startDate, endDate, periodType);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/company/{companyId}/department/{departmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<Map<String, Object>> getDepartmentAnalytics(
            @PathVariable Long companyId,
            @PathVariable Long departmentId) {
        
        try {
            Map<String, Object> analytics = analyticsService.getDepartmentAnalytics(companyId, departmentId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/company/{companyId}/department/{departmentId}/metric/{metricType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<AnalyticsResponseDTO> getDepartmentMetric(
            @PathVariable Long companyId,
            @PathVariable Long departmentId,
            @PathVariable AnalyticsData.MetricType metricType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") AnalyticsData.PeriodType periodType) {
        
        try {
            AnalyticsResponseDTO analytics = analyticsService.getAnalytics(
                    companyId, departmentId, metricType, startDate, endDate, periodType);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/company/{companyId}/generate/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateDailyAnalytics(@PathVariable Long companyId) {
        try {
            analyticsService.generateDailyAnalytics(companyId);
            return ResponseEntity.ok("Daily analytics generated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to generate daily analytics");
        }
    }

    @PostMapping("/company/{companyId}/generate/weekly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateWeeklyAnalytics(@PathVariable Long companyId) {
        try {
            analyticsService.generateWeeklyAnalytics(companyId);
            return ResponseEntity.ok("Weekly analytics generated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to generate weekly analytics");
        }
    }

    @PostMapping("/company/{companyId}/generate/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateMonthlyAnalytics(@PathVariable Long companyId) {
        try {
            analyticsService.generateMonthlyAnalytics(companyId);
            return ResponseEntity.ok("Monthly analytics generated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to generate monthly analytics");
        }
    }

    @GetMapping("/company/{companyId}/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<String> exportAnalytics(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "CSV") String format) {
        
        try {
            // TODO: Implement export functionality
            return ResponseEntity.ok("Export functionality coming soon");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to export analytics");
        }
    }
}
