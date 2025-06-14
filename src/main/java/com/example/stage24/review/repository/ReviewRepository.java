package com.example.stage24.review.repository;

import com.example.stage24.review.domain.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByArticleId(Long articleId);

    List<Review> findByUserId(Long userId);

    List<Review> findByRating(int rating);

    @Query("SELECT r FROM Review r WHERE r.article.id = :articleId ORDER BY r.createdAt DESC")
    List<Review> findByArticleIdOrderByCreatedAtDesc(@Param("articleId") Long articleId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.article.id = :articleId")
    Double findAverageRatingByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.article.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.article.id = :articleId")
    Optional<Double> getAverageRatingByArticleId(@Param("articleId") Long articleId);
}