/*
* -id: int
* -comment: String
* -createdAt: Date
* -userID: int
* -articleID: String
* -status: ReviewStatus
*/
package com.example.stage24.review.domain;

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

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private int userId;

    @NotNull
    private int articleId;

    @NotNull
    private ReviewStatus status;
}
