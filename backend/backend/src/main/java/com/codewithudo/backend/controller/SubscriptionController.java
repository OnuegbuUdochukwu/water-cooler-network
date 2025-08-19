package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.SubscriptionDTO;
import com.codewithudo.backend.entity.Subscription;
import com.codewithudo.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    /**
     * Create a new subscription
     */
    @PostMapping
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        try {
            SubscriptionDTO subscription = subscriptionService.createSubscription(
                request.getCompanyId(),
                request.getPlanType(),
                request.getBillingCycle()
            );
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get active subscription for a company
     */
    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<SubscriptionDTO> getActiveSubscription(@PathVariable Long companyId) {
        return subscriptionService.getActiveSubscription(companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all subscriptions for a company
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SubscriptionDTO>> getCompanySubscriptions(@PathVariable Long companyId) {
        List<SubscriptionDTO> subscriptions = subscriptionService.getCompanySubscriptions(companyId);
        return ResponseEntity.ok(subscriptions);
    }
    
    /**
     * Update subscription plan
     */
    @PutMapping("/{subscriptionId}/plan")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> updateSubscriptionPlan(
            @PathVariable Long subscriptionId,
            @Valid @RequestBody UpdatePlanRequest request) {
        try {
            SubscriptionDTO subscription = subscriptionService.updateSubscriptionPlan(
                subscriptionId, request.getPlanType());
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Cancel subscription
     */
    @PostMapping("/{subscriptionId}/cancel")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam(defaultValue = "true") boolean cancelAtPeriodEnd) {
        try {
            SubscriptionDTO subscription = subscriptionService.cancelSubscription(
                subscriptionId, cancelAtPeriodEnd);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Reactivate canceled subscription
     */
    @PostMapping("/{subscriptionId}/reactivate")
    @PreAuthorize("hasRole('CORPORATE_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDTO> reactivateSubscription(@PathVariable Long subscriptionId) {
        try {
            SubscriptionDTO subscription = subscriptionService.reactivateSubscription(subscriptionId);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get available plan types
     */
    @GetMapping("/plans")
    public ResponseEntity<List<Subscription.PlanType>> getAvailablePlans() {
        List<Subscription.PlanType> plans = subscriptionService.getAvailablePlanTypes();
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Get plan details
     */
    @GetMapping("/plans/{planType}")
    public ResponseEntity<Subscription.PlanType> getPlanDetails(@PathVariable String planType) {
        try {
            Subscription.PlanType plan = Subscription.PlanType.valueOf(planType.toUpperCase());
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Check if company can upgrade to plan
     */
    @GetMapping("/company/{companyId}/can-upgrade/{planType}")
    public ResponseEntity<Boolean> canUpgradeToPlan(
            @PathVariable Long companyId,
            @PathVariable String planType) {
        try {
            Subscription.PlanType plan = Subscription.PlanType.valueOf(planType.toUpperCase());
            boolean canUpgrade = subscriptionService.canUpgradeToPlan(companyId, plan);
            return ResponseEntity.ok(canUpgrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Request DTOs
    public static class CreateSubscriptionRequest {
        private Long companyId;
        private Subscription.PlanType planType;
        private Subscription.BillingCycle billingCycle;
        
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        
        public Subscription.PlanType getPlanType() { return planType; }
        public void setPlanType(Subscription.PlanType planType) { this.planType = planType; }
        
        public Subscription.BillingCycle getBillingCycle() { return billingCycle; }
        public void setBillingCycle(Subscription.BillingCycle billingCycle) { this.billingCycle = billingCycle; }
    }
    
    public static class UpdatePlanRequest {
        private Subscription.PlanType planType;
        
        public Subscription.PlanType getPlanType() { return planType; }
        public void setPlanType(Subscription.PlanType planType) { this.planType = planType; }
    }
}
