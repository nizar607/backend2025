package com.example.stage24.review.service.implementation;

import com.example.stage24.review.domain.Review;
import com.example.stage24.review.model.request.NewReview;
import com.example.stage24.review.model.response.ResponseReview;
import com.example.stage24.review.repository.ReviewRepository;
import com.example.stage24.review.service.interfaces.ReviewServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.article.domain.Article;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImplementation implements ReviewServiceInterface {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final SharedServiceInterface sharedService;

    @Override
    public Review addReview(NewReview newReview) {
        log.info("Adding new review for article ID: {}", newReview.getArticleId());

        Optional<User> user = sharedService.getConnectedUser();
        Optional<Article> article = articleRepository.findById(newReview.getArticleId());

        if (user.isEmpty()) {
            log.error("User not found.");
            throw new RuntimeException("User not found");
        }

        if (article.isEmpty()) {
            log.error("Article not found with ID: {}", newReview.getArticleId());
            throw new RuntimeException("Article not found");
        }

        Review review = new Review();
        review.setComment(newReview.getComment());
        review.setHelpful(newReview.getHelpful());
        review.setRating(newReview.getRating());
        review.setUser(user.get());
        review.setArticle(article.get());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        log.info("Review added successfully with ID: {}", savedReview.getId());
        return savedReview;
    }

    @Override
    public ResponseReview updateReview(Long id, ResponseReview responseReview) {
        log.info("Updating review with ID: {}", id);

        Optional<Review> existingReview = reviewRepository.findById(id);
        if (existingReview.isEmpty()) {
            log.error("Review not found with ID: {}", id);
            throw new RuntimeException("Review not found");
        }

        Review review = existingReview.get();
        review.setComment(responseReview.getComment());
        review.setHelpful(responseReview.getHelpful());
        review.setRating(responseReview.getRating());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        log.info("Review updated successfully with ID: {}", updatedReview.getId());
        return new ResponseReview(updatedReview);
    }

    @Override
    public void deleteReview(Long id) {
        log.info("Deleting review with ID: {}", id);

        if (!reviewRepository.existsById(id)) {
            log.error("Review not found with ID: {}", id);
            throw new RuntimeException("Review not found");
        }

        reviewRepository.deleteById(id);
        log.info("Review deleted successfully with ID: {}", id);
    }

    @Override
    public Optional<Review> getReview(Long id) {
        log.info("Fetching review with ID: {}", id);
        return reviewRepository.findById(id);
    }

    @Override
    public Optional<ResponseReview> getReviewResponse(Long id) {
        log.info("Fetching review response with ID: {}", id);
        Optional<Review> review = reviewRepository.findById(id);
        return review.map(ResponseReview::new);
    }

    @Override
    public List<Review> getAllReviews() {
        log.info("Fetching all reviews");
        return reviewRepository.findAll();
    }

    @Override
    public List<ResponseReview> getAllReviewsResponse() {
        log.info("Fetching all reviews as response");
        return reviewRepository.findAll().stream()
                .map(ResponseReview::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<ResponseReview>> getReviewsByArticleId(Long articleId) {
        log.info("Fetching reviews for article ID: {}", articleId);
        List<Review> reviews = reviewRepository.findByArticleId(articleId);
        if (reviews.isEmpty()) {
            return Optional.empty();
        }
        List<ResponseReview> responseReviews = reviews.stream()
                .map(ResponseReview::new)
                .collect(Collectors.toList());
        return Optional.of(responseReviews);
    }

    @Override
    public Optional<List<ResponseReview>> getReviewsByUserId(Long userId) {
        log.info("Fetching reviews for user ID: {}", userId);
        List<Review> reviews = reviewRepository.findByUserId(userId);
        if (reviews.isEmpty()) {
            return Optional.empty();
        }
        List<ResponseReview> responseReviews = reviews.stream()
                .map(ResponseReview::new)
                .collect(Collectors.toList());
        return Optional.of(responseReviews);
    }

    @Override
    public Optional<List<ResponseReview>> getReviewsByRating(int rating) {
        log.info("Fetching reviews with rating: {}", rating);
        List<Review> reviews = reviewRepository.findByRating(rating);
        if (reviews.isEmpty()) {
            return Optional.empty();
        }
        List<ResponseReview> responseReviews = reviews.stream()
                .map(ResponseReview::new)
                .collect(Collectors.toList());
        return Optional.of(responseReviews);
    }

    // getAverageRatingByArticleId
    @Override
    public double getAverageRatingByArticleId(Long articleId) {
        log.info("Fetching average rating for article ID: {}", articleId);
        return reviewRepository.getAverageRatingByArticleId(articleId).orElse(0.0);
    }

}