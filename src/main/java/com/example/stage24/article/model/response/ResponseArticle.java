package com.example.stage24.article.model.response;

import com.example.stage24.article.domain.Article;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseArticle {

    private long id;

    private String name;

    private String description;

    private double price;

    private long category;

    private String categoryName;

    private int quantity;

    private String image;

    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;

    private boolean isFavorite;

    private double averageRating = 0;

    public ResponseArticle(Article article) {
        this.id = article.getId();
        this.name = article.getName();
        this.description = article.getDescription();
        this.price = article.getPrice();
        this.category = article.getCategory().getId();
        this.categoryName = article.getCategory().getName();
        this.quantity = article.getQuantity();
        this.image = article.getImage();
        this.updatedAt = article.getUpdatedAt();
        this.createdAt = article.getCreatedAt();
        this.isFavorite = false; // Default value when user context is not available
    }

    public ResponseArticle(Article article, double averageRating) {
        this.id = article.getId();
        this.name = article.getName();
        this.description = article.getDescription();
        this.price = article.getPrice();
        this.category = article.getCategory().getId();
        this.categoryName = article.getCategory().getName();
        this.quantity = article.getQuantity();
        this.image = article.getImage();
        this.updatedAt = article.getUpdatedAt();
        this.createdAt = article.getCreatedAt();
        this.isFavorite = false;
        this.averageRating = Math.round(averageRating * 100.0) / 100.0;
    }

    public ResponseArticle(Article article, boolean isFavorite) {
        this.id = article.getId();
        this.name = article.getName();
        this.description = article.getDescription();
        this.price = article.getPrice();
        this.category = article.getCategory().getId();
        this.categoryName = article.getCategory().getName();
        this.quantity = article.getQuantity();
        this.image = article.getImage();
        this.updatedAt = article.getUpdatedAt();
        this.createdAt = article.getCreatedAt();
        this.isFavorite = isFavorite;
    }

}
