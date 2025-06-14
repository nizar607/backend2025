package com.example.stage24.favorite.domain;

import com.example.stage24.article.domain.Article;
import com.example.stage24.user.domain.User;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.Data;

// when registering a user automatically create a favorite list
@Data
@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Article article;
    
    // Track when the article was added to favorites
    @NotNull
    private LocalDateTime addedAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();
}