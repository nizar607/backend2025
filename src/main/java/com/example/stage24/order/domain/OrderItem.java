package com.example.stage24.order.domain;

import com.example.stage24.article.domain.Article;
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
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @NotBlank
    private String productName;

    private String productDescription;

    @NotNull
    private Double unitPrice;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double totalPrice; // unitPrice * quantity

    private String productImage;

    // Product details at time of order (for historical accuracy)
    private String productSku;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice * quantity;
        }
    }

    // Helper method to set product details from Article
    public void setProductDetailsFromArticle(Article article) {
        this.article = article;
        this.productName = article.getName();
        this.productDescription = article.getDescription();
        this.unitPrice = article.getPrice();
        this.productImage = article.getImage();
        // Set SKU if Article has it, otherwise generate or leave null
        calculateTotalPrice();
    }
}
