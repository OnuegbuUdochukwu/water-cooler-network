package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    /**
     * Find active subscription by company ID
     */
    @Query("SELECT s FROM Subscription s WHERE s.companyId = :companyId AND s.status IN ('ACTIVE', 'TRIALING')")
    Optional<Subscription> findActiveByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find all subscriptions by company ID
     */
    List<Subscription> findByCompanyId(Long companyId);
    
    /**
     * Find subscriptions by status
     */
    List<Subscription> findByStatus(Subscription.Status status);
    
    /**
     * Find subscriptions by plan type
     */
    List<Subscription> findByPlanType(Subscription.PlanType planType);
    
    /**
     * Find subscriptions by billing cycle
     */
    List<Subscription> findByBillingCycle(Subscription.BillingCycle billingCycle);
    
    /**
     * Find subscriptions that need renewal
     */
    @Query("SELECT s FROM Subscription s WHERE s.currentPeriodEnd <= :renewalDate AND s.status IN ('ACTIVE', 'TRIALING')")
    List<Subscription> findSubscriptionsNeedingRenewal(@Param("renewalDate") LocalDateTime renewalDate);
    
    /**
     * Find trial subscriptions that are ending soon
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'TRIALING' AND s.trialEnd <= :endDate")
    List<Subscription> findTrialSubscriptionsEndingSoon(@Param("endDate") LocalDateTime endDate);
    
    /**
     * Find past due subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'PAST_DUE' AND s.currentPeriodEnd < :currentDate")
    List<Subscription> findPastDueSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find subscriptions by Stripe subscription ID
     */
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    
    /**
     * Find subscriptions by Stripe customer ID
     */
    List<Subscription> findByStripeCustomerId(String stripeCustomerId);
    
    /**
     * Count active subscriptions by plan type
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.planType = :planType AND s.status IN ('ACTIVE', 'TRIALING')")
    Long countActiveByPlanType(@Param("planType") Subscription.PlanType planType);
    
    /**
     * Find subscriptions expiring in the next X days
     */
    @Query("SELECT s FROM Subscription s WHERE s.currentPeriodEnd BETWEEN :startDate AND :endDate AND s.status IN ('ACTIVE', 'TRIALING')")
    List<Subscription> findSubscriptionsExpiringBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find subscriptions with specific metadata
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata LIKE %:metadata%")
    List<Subscription> findByMetadataContaining(@Param("metadata") String metadata);
}
