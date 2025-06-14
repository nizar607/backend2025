package com.example.stage24.favorite.repository;

import com.example.stage24.favorite.domain.Favorite;
import com.example.stage24.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);
    
    Optional<Favorite> findByUser(User user);
    
    @Query("SELECT f FROM Favorite f WHERE f.article.id = :articleId")
    List<Favorite> findByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT f FROM Favorite f WHERE f.article.id = :articleId ORDER BY f.createdAt DESC")
    List<Favorite> findByArticleIdOrderByCreatedAtDesc(@Param("articleId") Long articleId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Favorite> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.article.id = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT f FROM Favorite f WHERE f.user = :user AND f.article.id = :articleId")
    Optional<Favorite> findByUserAndArticleId(@Param("user") User user, @Param("articleId") Long articleId);
    
    Optional<Favorite> findByUserAndArticle(User user, com.example.stage24.article.domain.Article article);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Favorite f WHERE f.article.id = :articleId AND f.user = :user")
    void deleteByArticleIdAndUser(@Param("articleId") Long articleId, @Param("user") User user);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.user = :user")
    int countByUser(@Param("user") User user);
    
}