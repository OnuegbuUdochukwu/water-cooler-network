package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Subscription;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubscriptionDTO {
    
    private Long id;
    private Long companyId;
    private Subscription.PlanType planType;
    private Subscription.Status status;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private Boolean cancelAtPeriodEnd;
    private LocalDateTime canceledAt;
    private LocalDateTime endedAt;
    private LocalDateTime trialStart;
    private LocalDateTime trialEnd;
    private BigDecimal amount;
    private String currency;
    private Subscription.BillingCycle billingCycle;
    private String stripeSubscriptionId;
    private String stripeCustomerId;
    private String paymentMethodId;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public SubscriptionDTO() {}
    
    public SubscriptionDTO(Long companyId, Subscription.PlanType planType, Subscription.BillingCycle billingCycle) {
        this.companyId = companyId;
        this.planType = planType;
        this.billingCycle = billingCycle;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
    
    public Subscription.PlanType getPlanType() {
        return planType;
    }
    
    public void setPlanType(Subscription.PlanType planType) {
        this.planType = planType;
    }
    
    public Subscription.Status getStatus() {
        return status;
    }
    
    public void setStatus(Subscription.Status status) {
        this.status = status;
    }
    
    public LocalDateTime getCurrentPeriodStart() {
        return currentPeriodStart;
    }
    
    public void setCurrentPeriodStart(LocalDateTime currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }
    
    public LocalDateTime getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }
    
    public void setCurrentPeriodEnd(LocalDateTime currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }
    
    public Boolean getCancelAtPeriodEnd() {
        return cancelAtPeriodEnd;
    }
    
    public void setCancelAtPeriodEnd(Boolean cancelAtPeriodEnd) {
        this.cancelAtPeriodEnd = cancelAtPeriodEnd;
    }
    
    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }
    
    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
    
    public LocalDateTime getEndedAt() {
        return endedAt;
    }
    
    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
    
    public LocalDateTime getTrialStart() {
        return trialStart;
    }
    
    public void setTrialStart(LocalDateTime trialStart) {
        this.trialStart = trialStart;
    }
    
    public LocalDateTime getTrialEnd() {
        return trialEnd;
    }
    
    public void setTrialEnd(LocalDateTime trialEnd) {
        this.trialEnd = trialEnd;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Subscription.BillingCycle getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(Subscription.BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }
    
    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }
    
    public String getStripeCustomerId() {
        return stripeCustomerId;
    }
    
    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }
    
    public String getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean isActive() {
        return status == Subscription.Status.ACTIVE || status == Subscription.Status.TRIALING;
    }
    
    public boolean isTrialActive() {
        return trialEnd != null && LocalDateTime.now().isBefore(trialEnd);
    }
    
    public boolean isCanceled() {
        return status == Subscription.Status.CANCELED || cancelAtPeriodEnd;
    }
    
    public boolean isPastDue() {
        return status == Subscription.Status.PAST_DUE;
    }
    
    public LocalDateTime getNextBillingDate() {
        if (isCanceled() && !cancelAtPeriodEnd) {
            return null;
        }
        return currentPeriodEnd;
    }
    
    public BigDecimal getAnnualAmount() {
        if (billingCycle == Subscription.BillingCycle.YEARLY) {
            return amount;
        } else {
            return amount.multiply(BigDecimal.valueOf(12));
        }
    }
    
    public BigDecimal getMonthlyAmount() {
        if (billingCycle == Subscription.BillingCycle.MONTHLY) {
            return amount;
        } else {
            return amount.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case ACTIVE -> "Active";
            case TRIALING -> "Trial";
            case PAST_DUE -> "Past Due";
            case CANCELED -> "Canceled";
            case UNPAID -> "Unpaid";
        };
    }
    
    public String getStatusColor() {
        return switch (status) {
            case ACTIVE -> "#28a745";
            case TRIALING -> "#17a2b8";
            case PAST_DUE -> "#ffc107";
            case CANCELED -> "#6c757d";
            case UNPAID -> "#dc3545";
        };
    }
    
    public String getPlanDisplayName() {
        return planType.getDisplayName();
    }
    
    public Integer getMaxEmployees() {
        return planType.getMaxEmployees();
    }
    
    public Boolean getHasAdvancedFeatures() {
        return planType.getHasAdvancedFeatures();
    }
    
    public Boolean getHasAnalytics() {
        return planType.getHasAnalytics();
    }
    
    public Boolean getHasPrioritySupport() {
        return planType.getHasPrioritySupport();
    }
}
