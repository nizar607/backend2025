package com.example.stage24.basket.model.response;

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

    private int quantity;

    private String image;

    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;

    public ResponseArticle(Article article) {
        this.id = article.getId();
        this.name = article.getName();
        this.description = article.getDescription();
        this.price = article.getPrice();
        this.category = article.getCategory().getId();
        this.quantity = article.getQuantity();
        this.image = article.getImage();
        this.updatedAt = article.getUpdatedAt();
        this.createdAt = article.getCreatedAt();
    }

}
