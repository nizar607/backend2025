package com.example.stage24.review.service.interfaces;

import com.example.stage24.review.domain.Review;
import com.example.stage24.review.model.request.NewReview;
import com.example.stage24.review.model.response.ResponseReview;

import java.util.Optional;
import java.util.List;

public interface ReviewServiceInterface {

    public Review addReview(NewReview review);

    public ResponseReview updateReview(Long id, ResponseReview review);

    public void deleteReview(Long id);

    public Optional<Review> getReview(Long id);

    public Optional<ResponseReview> getReviewResponse(Long id);

    public List<Review> getAllReviews();

    public List<ResponseReview> getAllReviewsResponse();

    public Optional<List<ResponseReview>> getReviewsByArticleId(Long articleId);

    public Optional<List<ResponseReview>> getReviewsByUserId(Long userId);

    public Optional<List<ResponseReview>> getReviewsByRating(int rating);

    public double getAverageRatingByArticleId(Long articleId);
}
