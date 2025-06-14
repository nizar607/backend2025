package com.example.stage24.invoice.domain;

import com.example.stage24.article.domain.Article;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoice_items")
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    private Invoice invoice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    // Product details (copied for historical accuracy)
    @NotNull
    private String productName;

    private String productDescription;

    // Pricing and quantity
    @NotNull
    private Double unitPrice;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double totalPrice;

    // Tax information
    private Double taxRate = 0.0;

    private Double taxAmount = 0.0;

    // Discount information
    private Double discountRate = 0.0;

    private Double discountAmount = 0.0;

    // Line total (after tax and discount)
    @NotNull
    private Double lineTotal;

    // Timestamps
    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    // Helper method to calculate totals
    public void calculateTotals() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice * quantity;
            
            // Calculate discount
            if (discountRate != null && discountRate > 0) {
                discountAmount = totalPrice * (discountRate / 100);
            } else {
                discountAmount = 0.0;
            }
            
            Double subtotal = totalPrice - discountAmount;
            
            // Calculate tax
            if (taxRate != null && taxRate > 0) {
                taxAmount = subtotal * (taxRate / 100);
            } else {
                taxAmount = 0.0;
            }
            
            lineTotal = subtotal + taxAmount;
        }
    }

    // Helper method to set product details from article
    public void setProductDetailsFromArticle(Article article) {
        if (article != null) {
            this.productName = article.getName();
            this.productDescription = article.getDescription();
            this.unitPrice = article.getPrice();
        }
    }

    // Constructor with article and quantity
    public InvoiceItem(Invoice invoice, Article article, Integer quantity) {
        this.invoice = invoice;
        this.article = article;
        this.quantity = quantity;
        setProductDetailsFromArticle(article);
    }

    // Constructor with article, quantity, and custom unit price
    public InvoiceItem(Invoice invoice, Article article, Integer quantity, Double unitPrice) {
        this.invoice = invoice;
        this.article = article;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        setProductDetailsFromArticle(article);
        this.unitPrice = unitPrice; // Override with custom price
    }
}