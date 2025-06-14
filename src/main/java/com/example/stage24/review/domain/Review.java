/*
* -id: int
* -comment: String
* -createdAt: Date
* -userID: int
* -articleID: String
* -status: ReviewStatus
*/
package com.example.stage24.review.domain;

import com.example.stage24.article.domain.Article;
import com.example.stage24.user.domain.User;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String comment;

    private int helpful;

    private int rating;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Article article;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();
}
