package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;
    
    @Column(name = "current_period_start", nullable = false)
    private LocalDateTime currentPeriodStart;
    
    @Column(name = "current_period_end", nullable = false)
    private LocalDateTime currentPeriodEnd;
    
    @Column(name = "cancel_at_period_end", nullable = false)
    private Boolean cancelAtPeriodEnd = false;
    
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Column(name = "trial_start")
    private LocalDateTime trialStart;
    
    @Column(name = "trial_end")
    private LocalDateTime trialEnd;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "USD";
    
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle = BillingCycle.MONTHLY;
    
    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
    
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
    
    @Column(name = "payment_method_id")
    private String paymentMethodId;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum PlanType {
        FREE("Free", 0, 10, false, false, false),
        BASIC("Basic", 29, 50, true, true, false),
        PREMIUM("Premium", 79, 200, true, true, true),
        ENTERPRISE("Enterprise", 199, 1000, true, true, true);
        
        private final String displayName;
        private final Integer price;
        private final Integer maxEmployees;
        private final Boolean hasAdvancedFeatures;
        private final Boolean hasAnalytics;
        private final Boolean hasPrioritySupport;
        
        PlanType(String displayName, Integer price, Integer maxEmployees, 
                Boolean hasAdvancedFeatures, Boolean hasAnalytics, Boolean hasPrioritySupport) {
            this.displayName = displayName;
            this.price = price;
            this.maxEmployees = maxEmployees;
            this.hasAdvancedFeatures = hasAdvancedFeatures;
            this.hasAnalytics = hasAnalytics;
            this.hasPrioritySupport = hasPrioritySupport;
        }
        
        public String getDisplayName() { return displayName; }
        public Integer getPrice() { return price; }
        public Integer getMaxEmployees() { return maxEmployees; }
        public Boolean getHasAdvancedFeatures() { return hasAdvancedFeatures; }
        public Boolean getHasAnalytics() { return hasAnalytics; }
        public Boolean getHasPrioritySupport() { return hasPrioritySupport; }
    }
    
    public enum Status {
        ACTIVE, PAST_DUE, CANCELED, UNPAID, TRIALING
    }
    
    public enum BillingCycle {
        MONTHLY, YEARLY
    }
    
    // Helper methods
    public boolean isActive() {
        return status == Status.ACTIVE || status == Status.TRIALING;
    }
    
    public boolean isTrialActive() {
        return trialEnd != null && LocalDateTime.now().isBefore(trialEnd);
    }
    
    public boolean isCanceled() {
        return status == Status.CANCELED || cancelAtPeriodEnd;
    }
    
    public boolean isPastDue() {
        return status == Status.PAST_DUE;
    }
    
    public LocalDateTime getNextBillingDate() {
        if (isCanceled() && !cancelAtPeriodEnd) {
            return null;
        }
        return currentPeriodEnd;
    }
    
    public BigDecimal getAnnualAmount() {
        if (billingCycle == BillingCycle.YEARLY) {
            return amount;
        } else {
            return amount.multiply(BigDecimal.valueOf(12));
        }
    }
    
    public BigDecimal getMonthlyAmount() {
        if (billingCycle == BillingCycle.MONTHLY) {
            return amount;
        } else {
            return amount.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
