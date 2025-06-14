package com.example.stage24.order.domain;

import com.example.stage24.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @NotBlank
    private String orderNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotNull
    private Double subtotalAmount;

    @NotNull
    private Double taxAmount;

    @NotNull
    private Double shippingAmount;

    @NotNull
    private Double totalAmount;

    private Double discountAmount = 0.0;

    @NotBlank
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Shipping Information
    @NotBlank
    private String shippingAddress;

    private String shippingCity;

    private String shippingState;

    private String shippingZipCode;

    private String shippingCountry;

    // Billing Information
    private String billingAddress;

    private String billingCity;

    private String billingState;

    private String billingZipCode;

    private String billingCountry;

    private String customerNotes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderStatus == null) {
            orderStatus = OrderStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to generate order number
    public void generateOrderNumber() {
        if (this.orderNumber == null || this.orderNumber.isEmpty()) {
            this.orderNumber = "ORD-" + System.currentTimeMillis();
        }
    }

    // Helper methods for status management
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED,
        PAYMENT_FAILED
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED,
        PARTIALLY_REFUNDED
    }
}
