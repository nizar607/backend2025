package com.example.stage24.payment.domain;

import com.example.stage24.order.domain.Order;
import com.example.stage24.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @NotBlank
    private String paymentReference; // Unique payment identifier

    @NotBlank
    private String transactionId; // External payment gateway transaction ID

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @NotNull
    private Double amount;

    private String currency = "USD";

    // Payment gateway details
    private String gatewayName; // e.g., "Stripe", "PayPal", "Square"

    private String gatewayTransactionId;

    private String gatewayResponse; // JSON response from gateway

    // Card details (if applicable) - should be encrypted in production
    private String cardLast4;

    private String cardBrand; // Visa, MasterCard, etc.

    private String cardExpiryMonth;

    private String cardExpiryYear;

    // Billing information
    private String billingName;

    private String billingEmail;

    private String billingAddress;

    private String billingCity;

    private String billingState;

    private String billingZipCode;

    private String billingCountry;

    // Timestamps
    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    private LocalDateTime processedAt;

    private LocalDateTime failedAt;

    private LocalDateTime refundedAt;

    // Failure details
    private String failureReason;

    private String failureCode;

    // Refund details
    private Double refundAmount;

    private String refundReason;

    private String refundTransactionId;

    // Retry mechanism
    private Integer retryCount = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Set timestamps based on status changes
        if (status == PaymentStatus.COMPLETED && processedAt == null) {
            processedAt = LocalDateTime.now();
        } else if (status == PaymentStatus.FAILED && failedAt == null) {
            failedAt = LocalDateTime.now();
        } else if (status == PaymentStatus.REFUNDED && refundedAt == null) {
            refundedAt = LocalDateTime.now();
        }
    }

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        BANK_TRANSFER,
        CASH_ON_DELIVERY,
        DIGITAL_WALLET,
        CRYPTOCURRENCY,
        REFUND
    }

    // Helper method to generate transaction ID
    public void generateTransactionId() {
        if (this.transactionId == null || this.transactionId.isEmpty()) {
            this.transactionId = "TXN-" + System.currentTimeMillis();
        }
    }

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REFUNDED,
        PARTIALLY_REFUNDED
    }
}