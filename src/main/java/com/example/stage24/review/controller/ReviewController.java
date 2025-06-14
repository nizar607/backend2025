package com.example.stage24.review.controller;

import com.example.stage24.review.domain.Review;
import com.example.stage24.review.model.request.NewReview;
import com.example.stage24.review.model.response.ResponseReview;
import com.example.stage24.review.service.interfaces.ReviewServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewServiceInterface reviewService;

    // Create new review
    @PostMapping
    public ResponseEntity<ResponseReview> createReview(@Valid @RequestBody NewReview review) {
        log.info("Creating new review: {}", review);
        try {
            Review savedReview = reviewService.addReview(review);
            ResponseReview responseReview = new ResponseReview(savedReview);
            return new ResponseEntity<>(responseReview, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating review: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update review
    @PutMapping("/{id}")
    public ResponseEntity<ResponseReview> updateReview(@PathVariable("id") Long id,
            @RequestBody ResponseReview review) {
        log.info("Updating review with ID: {}", id);
        try {
            ResponseReview updatedReview = reviewService.updateReview(id, review);
            return new ResponseEntity<>(updatedReview, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating review: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all reviews
    @GetMapping
    public ResponseEntity<List<ResponseReview>> getAllReviews() {
        log.info("Fetching all reviews");
        List<ResponseReview> reviews = reviewService.getAllReviewsResponse();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    // Get review by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseReview> getReviewById(@PathVariable("id") Long id) {
        log.info("Fetching review with ID: {}", id);
        Optional<ResponseReview> review = reviewService.getReviewResponse(id);
        return review.map(responseReview -> new ResponseEntity<>(responseReview, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get reviews by article ID

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<ResponseReview>> getReviewsByArticleId(@PathVariable("articleId") Long articleId) {
        log.info("Fetching reviews for article ID: {}", articleId);
        Optional<List<ResponseReview>> reviews = reviewService.getReviewsByArticleId(articleId);
        List<ResponseReview> emptyList = new ArrayList<>();
        return reviews.map(responseReviews -> new ResponseEntity<>(responseReviews, HttpStatus.OK))
                .orElse(new ResponseEntity<>(emptyList, HttpStatus.OK));
    }

    // Get reviews by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResponseReview>> getReviewsByUserId(@PathVariable("userId") Long userId) {
        log.info("Fetching reviews for user ID: {}", userId);
        Optional<List<ResponseReview>> reviews = reviewService.getReviewsByUserId(userId);
        return reviews.map(responseReviews -> new ResponseEntity<>(responseReviews, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get reviews by rating
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ResponseReview>> getReviewsByRating(@PathVariable("rating") int rating) {
        log.info("Fetching reviews with rating: {}", rating);
        Optional<List<ResponseReview>> reviews = reviewService.getReviewsByRating(rating);
        return reviews.map(responseReviews -> new ResponseEntity<>(responseReviews, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteReview(@PathVariable("id") Long id) {
        log.info("Deleting review with ID: {}", id);
        try {
            reviewService.deleteReview(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting review: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/article/{articleId}/average")
    public ResponseEntity<Double> getAverageRatingByArticleId(@PathVariable("articleId") Long articleId) {
        log.info("Fetching average rating for article ID: {}", articleId);
        Double averageRating = reviewService.getAverageRatingByArticleId(articleId);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }
}
