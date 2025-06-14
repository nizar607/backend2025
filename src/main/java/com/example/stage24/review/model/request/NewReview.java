package com.example.stage24.review.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewReview {

    private String comment;

    private int helpful;

    private int rating;

    private Long articleId;

}
