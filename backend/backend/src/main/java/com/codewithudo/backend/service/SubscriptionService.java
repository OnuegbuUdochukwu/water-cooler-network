package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.SubscriptionDTO;
import com.codewithudo.backend.entity.Subscription;
import com.codewithudo.backend.entity.Company;
import com.codewithudo.backend.repository.SubscriptionRepository;
import com.codewithudo.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final CompanyRepository companyRepository;
    
    /**
     * Create a new subscription for a company
     */
    @Transactional
    public SubscriptionDTO createSubscription(Long companyId, Subscription.PlanType planType, 
                                           Subscription.BillingCycle billingCycle) {
        // Check if company exists
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isEmpty()) {
            throw new RuntimeException("Company not found");
        }
        
        // Check if company already has an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findActiveByCompanyId(companyId);
        if (existingSubscription.isPresent()) {
            throw new RuntimeException("Company already has an active subscription");
        }
        
        // Create new subscription
        Subscription subscription = new Subscription();
        subscription.setCompanyId(companyId);
        subscription.setPlanType(planType);
        subscription.setBillingCycle(billingCycle);
        subscription.setStatus(Subscription.Status.TRIALING);
        
        // Set trial period (14 days)
        LocalDateTime now = LocalDateTime.now();
        subscription.setTrialStart(now);
        subscription.setTrialEnd(now.plusDays(14));
        
        // Set billing period
        subscription.setCurrentPeriodStart(now);
        if (billingCycle == Subscription.BillingCycle.MONTHLY) {
            subscription.setCurrentPeriodEnd(now.plusMonths(1));
        } else {
            subscription.setCurrentPeriodEnd(now.plusYears(1));
        }
        
        // Set amount based on plan and billing cycle
        BigDecimal baseAmount = BigDecimal.valueOf(planType.getPrice());
        if (billingCycle == Subscription.BillingCycle.YEARLY) {
            // Apply 20% discount for yearly billing
            baseAmount = baseAmount.multiply(BigDecimal.valueOf(12))
                                 .multiply(BigDecimal.valueOf(0.8));
        }
        subscription.setAmount(baseAmount);
        
        subscription = subscriptionRepository.save(subscription);
        
        // Update company subscription tier
        Company companyEntity = company.get();
        companyEntity.setSubscriptionTier(Company.SubscriptionTier.valueOf(planType.name()));
        companyRepository.save(companyEntity);
        
        log.info("Created subscription for company {}: {} plan, {} billing", 
                companyId, planType, billingCycle);
        
        return convertToDto(subscription);
    }
    
    /**
     * Get active subscription for a company
     */
    public Optional<SubscriptionDTO> getActiveSubscription(Long companyId) {
        return subscriptionRepository.findActiveByCompanyId(companyId)
                .map(this::convertToDto);
    }
    
    /**
     * Get all subscriptions for a company
     */
    public List<SubscriptionDTO> getCompanySubscriptions(Long companyId) {
        return subscriptionRepository.findByCompanyId(companyId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update subscription plan
     */
    @Transactional
    public SubscriptionDTO updateSubscriptionPlan(Long subscriptionId, Subscription.PlanType newPlanType) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isEmpty()) {
            throw new RuntimeException("Subscription not found");
        }
        
        Subscription subscription = subscriptionOpt.get();
        
        // Check if subscription is active
        if (!subscription.isActive()) {
            throw new RuntimeException("Cannot update inactive subscription");
        }
        
        // Update plan type
        subscription.setPlanType(newPlanType);
        
        // Recalculate amount
        BigDecimal baseAmount = BigDecimal.valueOf(newPlanType.getPrice());
        if (subscription.getBillingCycle() == Subscription.BillingCycle.YEARLY) {
            baseAmount = baseAmount.multiply(BigDecimal.valueOf(12))
                                 .multiply(BigDecimal.valueOf(0.8));
        }
        subscription.setAmount(baseAmount);
        
        subscription = subscriptionRepository.save(subscription);
        
        // Update company subscription tier
        Optional<Company> company = companyRepository.findById(subscription.getCompanyId());
        if (company.isPresent()) {
            Company companyEntity = company.get();
            companyEntity.setSubscriptionTier(Company.SubscriptionTier.valueOf(newPlanType.name()));
            companyRepository.save(companyEntity);
        }
        
        log.info("Updated subscription {} to {} plan", subscriptionId, newPlanType);
        
        return convertToDto(subscription);
    }
    
    /**
     * Cancel subscription
     */
    @Transactional
    public SubscriptionDTO cancelSubscription(Long subscriptionId, boolean cancelAtPeriodEnd) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isEmpty()) {
            throw new RuntimeException("Subscription not found");
        }
        
        Subscription subscription = subscriptionOpt.get();
        
        if (cancelAtPeriodEnd) {
            subscription.setCancelAtPeriodEnd(true);
            log.info("Subscription {} will be canceled at period end", subscriptionId);
        } else {
            subscription.setStatus(Subscription.Status.CANCELED);
            subscription.setCanceledAt(LocalDateTime.now());
            subscription.setEndedAt(LocalDateTime.now());
            
            // Update company subscription tier to FREE
            Optional<Company> company = companyRepository.findById(subscription.getCompanyId());
            if (company.isPresent()) {
                Company companyEntity = company.get();
                companyEntity.setSubscriptionTier(Company.SubscriptionTier.FREE);
                companyRepository.save(companyEntity);
            }
            
            log.info("Subscription {} canceled immediately", subscriptionId);
        }
        
        subscription = subscriptionRepository.save(subscription);
        return convertToDto(subscription);
    }
    
    /**
     * Reactivate canceled subscription
     */
    @Transactional
    public SubscriptionDTO reactivateSubscription(Long subscriptionId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isEmpty()) {
            throw new RuntimeException("Subscription not found");
        }
        
        Subscription subscription = subscriptionOpt.get();
        
        if (subscription.getStatus() != Subscription.Status.CANCELED) {
            throw new RuntimeException("Subscription is not canceled");
        }
        
        subscription.setStatus(Subscription.Status.ACTIVE);
        subscription.setCancelAtPeriodEnd(false);
        subscription.setCanceledAt(null);
        subscription.setEndedAt(null);
        
        // Set new billing period
        LocalDateTime now = LocalDateTime.now();
        subscription.setCurrentPeriodStart(now);
        if (subscription.getBillingCycle() == Subscription.BillingCycle.MONTHLY) {
            subscription.setCurrentPeriodEnd(now.plusMonths(1));
        } else {
            subscription.setCurrentPeriodEnd(now.plusYears(1));
        }
        
        subscription = subscriptionRepository.save(subscription);
        
        log.info("Reactivated subscription {}", subscriptionId);
        
        return convertToDto(subscription);
    }
    
    /**
     * Get available plan types
     */
    public List<Subscription.PlanType> getAvailablePlanTypes() {
        return List.of(Subscription.PlanType.values());
    }
    
    /**
     * Get plan details
     */
    public Subscription.PlanType getPlanDetails(Subscription.PlanType planType) {
        return planType;
    }
    
    /**
     * Check if company can upgrade to plan
     */
    public boolean canUpgradeToPlan(Long companyId, Subscription.PlanType planType) {
        Optional<Subscription> currentSubscription = subscriptionRepository.findActiveByCompanyId(companyId);
        
        if (currentSubscription.isEmpty()) {
            return true; // No current subscription, can upgrade to any plan
        }
        
        Subscription current = currentSubscription.get();
        
        // Check if new plan has higher features
        return planType.getPrice() > current.getPlanType().getPrice();
    }
    
    /**
     * Process subscription renewal
     */
    @Transactional
    public void processSubscriptionRenewal(Long subscriptionId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isEmpty()) {
            return;
        }
        
        Subscription subscription = subscriptionOpt.get();
        
        if (!subscription.isActive() || subscription.isCanceled()) {
            return;
        }
        
        // Set new billing period
        LocalDateTime now = LocalDateTime.now();
        subscription.setCurrentPeriodStart(now);
        
        if (subscription.getBillingCycle() == Subscription.BillingCycle.MONTHLY) {
            subscription.setCurrentPeriodEnd(now.plusMonths(1));
        } else {
            subscription.setCurrentPeriodEnd(now.plusYears(1));
        }
        
        // If trial ended, change status to ACTIVE
        if (subscription.getStatus() == Subscription.Status.TRIALING && 
            subscription.getTrialEnd() != null && 
            now.isAfter(subscription.getTrialEnd())) {
            subscription.setStatus(Subscription.Status.ACTIVE);
        }
        
        subscriptionRepository.save(subscription);
        
        log.info("Processed renewal for subscription {}", subscriptionId);
    }
    
    /**
     * Convert entity to DTO
     */
    private SubscriptionDTO convertToDto(Subscription subscription) {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());
        dto.setCompanyId(subscription.getCompanyId());
        dto.setPlanType(subscription.getPlanType());
        dto.setStatus(subscription.getStatus());
        dto.setCurrentPeriodStart(subscription.getCurrentPeriodStart());
        dto.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
        dto.setCancelAtPeriodEnd(subscription.getCancelAtPeriodEnd());
        dto.setCanceledAt(subscription.getCanceledAt());
        dto.setEndedAt(subscription.getEndedAt());
        dto.setTrialStart(subscription.getTrialStart());
        dto.setTrialEnd(subscription.getTrialEnd());
        dto.setAmount(subscription.getAmount());
        dto.setCurrency(subscription.getCurrency());
        dto.setBillingCycle(subscription.getBillingCycle());
        dto.setStripeSubscriptionId(subscription.getStripeSubscriptionId());
        dto.setStripeCustomerId(subscription.getStripeCustomerId());
        dto.setPaymentMethodId(subscription.getPaymentMethodId());
        dto.setMetadata(subscription.getMetadata());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        
        return dto;
    }
}
