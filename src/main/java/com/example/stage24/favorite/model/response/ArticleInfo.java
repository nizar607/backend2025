package com.example.stage24.favorite.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleInfo {
    private Long id;
    private String name;
    private String categoryName;
    private double price;
    private Double averageRating;
    private Long reviewCount;
    private LocalDateTime addedToFavoritesAt;
    private LocalDateTime articleCreatedAt;
    private String imageUrl;

}