package com.example.stage24.invoice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "invoice_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Invoice invoice;

    // Article/Article Information
    @NotNull
    private Long articleId;

    @NotBlank
    private String articleName;

    @Column(columnDefinition = "TEXT")
    private String articleDescription;
    private String articleCategory;
    private String articleImageUrl;

    // Pricing and Quantity
    @NotNull
    @Positive

    private double unitPrice;

    @NotNull
    @Positive
    private Integer quantity;

    // Calculated fields
    @NotNull

    private double lineTotal;

    // Tax Information

    private double taxRate = 0.0;

    private double taxAmount = 0.0;

    // Timestamps
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateLineTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateLineTotal();
    }

    // Helper method to calculate line total
    public void calculateLineTotal() {

        double subtotal = unitPrice * quantity;

        // Calculate tax
        if (taxRate > 0) {
            taxAmount = subtotal * (taxRate / 100);
        } else {
            taxAmount = 0.0;
        }

        lineTotal = subtotal + taxAmount;

    }

    // Helper method to set article details from an Article entity
    public void setArticleDetailsFromArticle(Object article) {
        // This method can be implemented when Article entity is available
        // For now, it's a placeholder for future integration
    }

    // Constructor for creating invoice item with basic details
    public InvoiceItem(Long articleId, String articleName, Double unitPrice, Integer quantity) {
        this.articleId = articleId;
        this.articleName = articleName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        calculateLineTotal();
    }

    // Constructor with discount
    public InvoiceItem(Long articleId, String articleName, Double unitPrice, Integer quantity,
            BigDecimal discountAmount) {
        this.articleId = articleId;
        this.articleName = articleName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        calculateLineTotal();
    }

    // Helper method to get subtotal before tax
    public double getSubtotal() {

        return unitPrice * quantity;
    }
}