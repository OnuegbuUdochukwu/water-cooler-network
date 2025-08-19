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
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;
    
    @Column(name = "stripe_charge_id")
    private String stripeChargeId;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum Status {
        PENDING, PROCESSING, SUCCEEDED, FAILED, CANCELED, REFUNDED
    }
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, WALLET, CRYPTO
    }
    
    // Helper methods
    public boolean isSuccessful() {
        return status == Status.SUCCEEDED;
    }
    
    public boolean isFailed() {
        return status == Status.FAILED;
    }
    
    public boolean isPending() {
        return status == Status.PENDING || status == Status.PROCESSING;
    }
    
    public boolean isRefundable() {
        return status == Status.SUCCEEDED && processedAt != null;
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case PENDING -> "Pending";
            case PROCESSING -> "Processing";
            case SUCCEEDED -> "Successful";
            case FAILED -> "Failed";
            case CANCELED -> "Canceled";
            case REFUNDED -> "Refunded";
        };
    }
    
    public String getStatusColor() {
        return switch (status) {
            case PENDING -> "#ffc107";
            case PROCESSING -> "#17a2b8";
            case SUCCEEDED -> "#28a745";
            case FAILED -> "#dc3545";
            case CANCELED -> "#6c757d";
            case REFUNDED -> "#fd7e14";
        };
    }
}
