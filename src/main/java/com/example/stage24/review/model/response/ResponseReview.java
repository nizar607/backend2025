package com.example.stage24.review.model.response;

import com.example.stage24.review.domain.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseReview {

    private Long id;

    private String comment;

    private int helpful;

    private int rating;

    private Long userId;

    private String userName;

    private Long articleId;

    private String articleName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ResponseReview(Review review) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.helpful = review.getHelpful();
        this.rating = review.getRating();
        this.userId = review.getUser().getId();
        this.userName = review.getUser().getFirstName() + " " + review.getUser().getLastName();
        this.articleId = review.getArticle().getId();
        this.articleName = review.getArticle().getName();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }

}
