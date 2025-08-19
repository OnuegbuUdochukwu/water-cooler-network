package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payments by subscription ID
     */
    List<Payment> findBySubscriptionId(Long subscriptionId);
    
    /**
     * Find payments by company ID
     */
    List<Payment> findByCompanyId(Long companyId);
    
    /**
     * Find payments by status
     */
    List<Payment> findByStatus(Payment.Status status);
    
    /**
     * Find payments by payment method
     */
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    /**
     * Find payments by Stripe payment intent ID
     */
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    /**
     * Find payments by Stripe charge ID
     */
    Optional<Payment> findByStripeChargeId(String stripeChargeId);
    
    /**
     * Find successful payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'SUCCEEDED'")
    List<Payment> findSuccessfulPayments();
    
    /**
     * Find failed payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED'")
    List<Payment> findFailedPayments();
    
    /**
     * Find payments within date range
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find payments by amount range
     */
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * Count payments by status
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") Payment.Status status);
    
    /**
     * Sum total payments by company
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.companyId = :companyId AND p.status = 'SUCCEEDED'")
    BigDecimal sumSuccessfulPaymentsByCompany(@Param("companyId") Long companyId);
    
    /**
     * Find payments with specific metadata
     */
    @Query("SELECT p FROM Payment p WHERE p.metadata LIKE %:metadata%")
    List<Payment> findByMetadataContaining(@Param("metadata") String metadata);
    
    /**
     * Find refundable payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.processedAt IS NOT NULL")
    List<Payment> findRefundablePayments();
    
    /**
     * Find payments by currency
     */
    List<Payment> findByCurrency(String currency);
    
    /**
     * Find payments processed on specific date
     */
    @Query("SELECT p FROM Payment p WHERE DATE(p.processedAt) = DATE(:date)")
    List<Payment> findByProcessedDate(@Param("date") LocalDateTime date);
}
