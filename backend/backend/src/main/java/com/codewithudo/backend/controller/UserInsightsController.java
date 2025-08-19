package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.UserInsightDTO;
import com.codewithudo.backend.entity.UserBehavior;
import com.codewithudo.backend.entity.UserInsight;
import com.codewithudo.backend.service.UserInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-insights")
@CrossOrigin(origins = "*")
public class UserInsightsController {

    @Autowired
    private UserInsightsService userInsightsService;

    // Behavior Tracking
    @PostMapping("/track")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<String> trackBehavior(
            @RequestParam Long userId,
            @RequestParam UserBehavior.BehaviorType behaviorType,
            @RequestParam String context) {
        try {
            userInsightsService.trackBehavior(userId, behaviorType, context);
            return ResponseEntity.ok("Behavior tracked successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to track behavior");
        }
    }

    @PostMapping("/track/advanced")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<String> trackAdvancedBehavior(
            @RequestParam Long userId,
            @RequestParam UserBehavior.BehaviorType behaviorType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) String targetType,
            @RequestParam String context,
            @RequestParam(required = false) Integer durationSeconds,
            @RequestParam(required = false) Double intensityScore) {
        try {
            if (targetId != null && targetType != null) {
                userInsightsService.trackBehavior(userId, behaviorType, targetId, targetType, context, durationSeconds, intensityScore);
            } else {
                userInsightsService.trackBehavior(userId, behaviorType, context);
            }
            return ResponseEntity.ok("Behavior tracked successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to track behavior");
        }
    }

    // Insights Generation and Retrieval
    @PostMapping("/generate/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<List<UserInsightDTO>> generateInsights(@PathVariable Long userId) {
        try {
            List<UserInsightDTO> insights = userInsightsService.generateUserInsights(userId);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserInsightDTO>> getUserInsights(@PathVariable Long userId) {
        try {
            List<UserInsightDTO> insights = userInsightsService.getUserInsights(userId);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserInsightDTO>> getUnreadInsights(@PathVariable Long userId) {
        try {
            List<UserInsightDTO> insights = userInsightsService.getUnreadInsights(userId);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}/type/{insightType}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserInsightDTO>> getInsightsByType(
            @PathVariable Long userId,
            @PathVariable UserInsight.InsightType insightType) {
        try {
            List<UserInsightDTO> insights = userInsightsService.getInsightsByType(userId, insightType);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Insight Management
    @PutMapping("/{insightId}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<UserInsightDTO> markInsightAsRead(@PathVariable Long insightId) {
        try {
            UserInsightDTO insight = userInsightsService.markInsightAsRead(insightId);
            if (insight != null) {
                return ResponseEntity.ok(insight);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{insightId}/actioned")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<UserInsightDTO> markInsightAsActioned(@PathVariable Long insightId) {
        try {
            UserInsightDTO insight = userInsightsService.markInsightAsActioned(insightId);
            if (insight != null) {
                return ResponseEntity.ok(insight);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{insightId}/feedback")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<UserInsightDTO> addInsightFeedback(
            @PathVariable Long insightId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        try {
            UserInsightDTO insight = userInsightsService.addInsightFeedback(insightId, rating, comment);
            if (insight != null) {
                return ResponseEntity.ok(insight);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Behavioral Analysis
    @GetMapping("/analysis/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR_MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> analyzeUserBehavior(@PathVariable Long userId) {
        try {
            Map<String, Object> analysis = userInsightsService.analyzeUserBehavior(userId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Batch Operations
    @PostMapping("/track/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<String> trackBatchBehaviors(@RequestBody List<BehaviorTrackingRequest> requests) {
        try {
            for (BehaviorTrackingRequest request : requests) {
                if (request.getTargetId() != null && request.getTargetType() != null) {
                    userInsightsService.trackBehavior(
                            request.getUserId(),
                            request.getBehaviorType(),
                            request.getTargetId(),
                            request.getTargetType(),
                            request.getContext(),
                            request.getDurationSeconds(),
                            request.getIntensityScore()
                    );
                } else {
                    userInsightsService.trackBehavior(
                            request.getUserId(),
                            request.getBehaviorType(),
                            request.getContext()
                    );
                }
            }
            return ResponseEntity.ok("Batch behaviors tracked successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to track batch behaviors");
        }
    }

    // DTO for batch behavior tracking
    public static class BehaviorTrackingRequest {
        private Long userId;
        private UserBehavior.BehaviorType behaviorType;
        private Long targetId;
        private String targetType;
        private String context;
        private Integer durationSeconds;
        private Double intensityScore;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public UserBehavior.BehaviorType getBehaviorType() { return behaviorType; }
        public void setBehaviorType(UserBehavior.BehaviorType behaviorType) { this.behaviorType = behaviorType; }

        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }

        public String getTargetType() { return targetType; }
        public void setTargetType(String targetType) { this.targetType = targetType; }

        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }

        public Integer getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

        public Double getIntensityScore() { return intensityScore; }
        public void setIntensityScore(Double intensityScore) { this.intensityScore = intensityScore; }
    }
}
