package com.example.stage24.invoice.domain;

import com.example.stage24.order.domain.Order;
import com.example.stage24.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "invoices")
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    
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
    @Column(unique = true)
    private String invoiceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    // Company Information
    private String companyName;
    
    private String companyAddress;

    private String companyEmail;

    private String companyPhoneNumber;

    private String companyTaxId;

    private String companyWebsite;

    // Customer Information (copied from order for historical accuracy)
    private String customerName;

    private String customerEmail;

    private String customerAddress;

    private String customerCity;

    private String customerState;

    private String customerZipCode;

    private String customerCountry;

    private String customerTaxId;

    // Financial Details
    @NotNull
    private Double subtotalAmount;

    @NotNull
    private Double taxAmount;

    private Double discountAmount = 0.0;

    private Double shippingAmount = 0.0;

    @NotNull
    private Double totalAmount;

    private String currency = "USD";

    // Payment Terms
    private Integer paymentTermsDays = 30; // Net 30 days

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate dueDate;

    private LocalDate paidDate;

    // Invoice Items
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> invoiceItems;

    // Notes and Terms
    private String notes;

    private String terms;

    private String footerText;

    // Timestamps
    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (status == null) {
            status = InvoiceStatus.DRAFT;
        }
        
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        
        if (dueDate == null && paymentTermsDays != null) {
            dueDate = issueDate.plusDays(paymentTermsDays);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        if (status == InvoiceStatus.PAID && paidDate == null) {
            paidDate = LocalDate.now();
        }
        
        if (status == InvoiceStatus.SENT && sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    // Helper method to generate invoice number
    public void generateInvoiceNumber() {
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            String year = String.valueOf(LocalDate.now().getYear());
            String month = String.format("%02d", LocalDate.now().getMonthValue());
            String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
            invoiceNumber = "INV-" + year + month + "-" + timestamp;
        }
    }

    // Helper method to copy customer details from order
    public void setCustomerDetailsFromOrder(Order order) {
        if (order != null && order.getUser() != null) {
            User customer = order.getUser();
            this.customerName = customer.getFirstName() + " " + customer.getLastName();
            this.customerEmail = customer.getEmail();
            this.customerAddress = order.getBillingAddress();
            this.customerCity = order.getBillingCity();
            this.customerState = order.getBillingState();
            this.customerZipCode = order.getBillingZipCode();
            this.customerCountry = order.getBillingCountry();
        }
    }

    public enum InvoiceStatus {
        DRAFT,
        SENT,
        VIEWED,
        PAID,
        OVERDUE,
        CANCELLED,
        REFUNDED
    }
}