package com.example.stage24.basket.model.response;

import com.example.stage24.basket.domain.Item;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResponseItem {
    private Long id;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Article fields
    private Long articleId;
    private String name;
    private String description;
    private double price;
    private String image;
    private String categoryName;
    
    public ResponseItem(Item item) {
        this.id = item.getId();
        this.quantity = item.getQuantity();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        
        // Map article fields
        if (item.getArticle() != null) {
            this.articleId = item.getArticle().getId();
            this.name = item.getArticle().getName();
            this.description = item.getArticle().getDescription();
            this.price = item.getArticle().getPrice();
            this.image = item.getArticle().getImage();
            if (item.getArticle().getCategory() != null) {
                this.categoryName = item.getArticle().getCategory().getName();
            }
        }
    }
}