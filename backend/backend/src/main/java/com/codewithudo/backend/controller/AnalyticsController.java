package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.AnalyticsOverviewDTO;
import com.codewithudo.backend.dto.UserInsightsDTO;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.service.AnalyticsService;
import com.codewithudo.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    private final UserService userService;
    
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CORPORATE_ADMIN')")
    public ResponseEntity<AnalyticsOverviewDTO> getPlatformOverview() {
        AnalyticsOverviewDTO overview = analyticsService.getPlatformOverview();
        return ResponseEntity.ok(overview);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CORPORATE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserInsightsDTO> getUserInsights(@PathVariable Long userId) {
        UserInsightsDTO insights = analyticsService.getUserInsights(userId);
        return ResponseEntity.ok(insights);
    }
    
    @GetMapping("/my-insights")
    public ResponseEntity<UserInsightsDTO> getMyInsights(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        UserInsightsDTO insights = analyticsService.getUserInsights(currentUser.getId());
        return ResponseEntity.ok(insights);
    }
    
    @PostMapping("/generate-daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateDailyAnalytics() {
        analyticsService.generateDailyAnalytics();
        return ResponseEntity.ok("Daily analytics generation triggered successfully");
    }
    
    @GetMapping("/platform/summary")
    public ResponseEntity<PlatformSummaryDTO> getPlatformSummary() {
        // Public endpoint for basic platform statistics
        AnalyticsOverviewDTO overview = analyticsService.getPlatformOverview();
        
        PlatformSummaryDTO summary = new PlatformSummaryDTO();
        summary.setTotalUsers(overview.getTotalUsers());
        summary.setTotalMatches(overview.getTotalMatches());
        summary.setTotalLounges(overview.getTotalLounges());
        summary.setAverageRating(overview.getAverageFeedbackRating());
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CORPORATE_ADMIN')")
    public ResponseEntity<TrendsDTO> getTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Implementation for custom date range trends
        TrendsDTO trends = new TrendsDTO();
        // Add trend calculation logic here
        
        return ResponseEntity.ok(trends);
    }
    
    // DTOs for additional endpoints
    public static class PlatformSummaryDTO {
        private Long totalUsers;
        private Long totalMatches;
        private Long totalLounges;
        private Double averageRating;
        
        // Getters and setters
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        
        public Long getTotalMatches() { return totalMatches; }
        public void setTotalMatches(Long totalMatches) { this.totalMatches = totalMatches; }
        
        public Long getTotalLounges() { return totalLounges; }
        public void setTotalLounges(Long totalLounges) { this.totalLounges = totalLounges; }
        
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }
    
    public static class TrendsDTO {
        // Add trend data fields as needed
        private String message = "Trends data implementation pending";
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
