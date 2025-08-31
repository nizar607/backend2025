package com.example.stage24.invoice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "invoices")
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String userPhone;
    private String userAddress;

    // Company Information
    private String companyName;
    private String companyAddress;
    private String companyEmail;
    private String companyPhone;
    private String companyWebsite;



    // Financial Details
    @NotNull
  
    private Double subtotalAmount = 0.0;

    @NotNull

    private Double taxAmount = 0.0;

    @NotNull

    private Double totalAmount = 0.0;

    @Column(length = 3)
    private String currency = "USD";

    // Tax rate as percentage (e.g., 8.5 for 8.5%)
    private Double taxRate = 0.0;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Invoice Items (Articles)
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String footerText;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            generateInvoiceNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        this.updatedAt = LocalDateTime.now();
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

    // Helper method to add invoice item
    public void addInvoiceItem(InvoiceItem item) {
        invoiceItems.add(item);
        item.setInvoice(this);
    }

    // Helper method to remove invoice item
    public void removeInvoiceItem(InvoiceItem item) {
        invoiceItems.remove(item);
        item.setInvoice(null);
    }

    // Helper method to calculate totals
    public void calculateTotals() {
        subtotalAmount = 0.;
         invoiceItems.stream()
                .map(InvoiceItem::getLineTotal)
                .mapToDouble(Double::doubleValue).sum();

        // Calculate tax
        if (taxRate > 0) {
            taxAmount = subtotalAmount * (taxRate / 100);
        } else {
            taxAmount = 0.0;
        }

        // Calculate total
        totalAmount = subtotalAmount + taxAmount;
    }

    public enum InvoiceStatus {
        PENDING,
        PAID,
        CANCELLED
    }
}