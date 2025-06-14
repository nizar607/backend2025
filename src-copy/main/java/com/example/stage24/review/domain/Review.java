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
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Article article;

    @NotNull
    private ReviewStatus status;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();
}
