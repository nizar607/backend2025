package com.example.stage24.favorite.service.implementation;

import com.example.stage24.favorite.domain.Favorite;
import com.example.stage24.favorite.model.request.NewFavorite;
import com.example.stage24.favorite.model.response.ArticleInfo;
import com.example.stage24.favorite.model.response.ResponseFavorite;
import com.example.stage24.favorite.repository.FavoriteRepository;
import com.example.stage24.favorite.service.interfaces.FavoriteServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.article.domain.Article;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import com.example.stage24.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImplementation implements FavoriteServiceInterface {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final SharedServiceInterface sharedService;
    private final ReviewRepository reviewRepository;

    @Override
    public Favorite addFavorite(NewFavorite newFavorite) {
        log.info("Adding new favorite with article ID: {}", newFavorite.getArticleId());

        Optional<User> user = sharedService.getConnectedUser();

        if (user.isEmpty()) {
            log.error("User not found.");
            throw new RuntimeException("User not found");
        }

        // Get the article
        Optional<Article> article = articleRepository.findById(newFavorite.getArticleId());

        if (this._isFavorite(article.get(), user.get()).get()) {
            throw new RuntimeException("favorite already exist ");
        }

        if (article.isEmpty()) {
            log.error("Article not found with ID: {}", newFavorite.getArticleId());
            throw new RuntimeException("Article not found with ID: " + newFavorite.getArticleId());
        }

        // Check if user already has this article as favorite
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndArticle(user.get(), article.get());

        if (existingFavorite.isPresent()) {
            log.info("User already has this article as favorite");
            return existingFavorite.get();
        }

        // Create new favorite
        Favorite favorite = new Favorite();
        favorite.setUser(user.get());
        favorite.setArticle(article.get());
        favorite.setAddedAt(LocalDateTime.now());
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setUpdatedAt(LocalDateTime.now());

        Favorite savedFavorite = favoriteRepository.save(favorite);
        log.info("Favorite saved successfully with ID: {}", savedFavorite.getId());
        return savedFavorite;
    }

    @Override
    public Favorite updateFavorite(Long id, NewFavorite newFavorite) {
        log.info("Updating favorite with ID: {}", id);

        Optional<Favorite> existingFavorite = favoriteRepository.findById(id);
        if (existingFavorite.isEmpty()) {
            log.error("Favorite not found with ID: {}", id);
            throw new RuntimeException("Favorite not found");
        }

        // Get the article
        Optional<Article> article = articleRepository.findById(newFavorite.getArticleId());
        if (article.isEmpty()) {
            log.error("Article not found with ID: {}", newFavorite.getArticleId());
            throw new RuntimeException("Article not found with ID: " + newFavorite.getArticleId());
        }

        Favorite favorite = existingFavorite.get();
        favorite.setArticle(article.get());
        favorite.setAddedAt(LocalDateTime.now()); // Reset the added time when updating
        favorite.setUpdatedAt(LocalDateTime.now());

        Favorite updatedFavorite = favoriteRepository.save(favorite);
        log.info("Favorite updated successfully with ID: {}", updatedFavorite.getId());
        return updatedFavorite;
    }

    @Override
    public void deleteFavorite(Long id) {
        log.info("Deleting favorite with ID: {}", id);

        Optional<Favorite> favorite = favoriteRepository.findById(id);
        if (favorite.isEmpty()) {
            log.error("Favorite not found with ID: {}", id);
            throw new RuntimeException("Favorite not found");
        }

        favoriteRepository.deleteById(id);
        log.info("Favorite deleted successfully with ID: {}", id);
    }

    @Override
    public Optional<Favorite> getFavorite(Long id) {
        log.info("Fetching favorite with ID: {}", id);
        return favoriteRepository.findById(id);
    }

    @Override
    public Optional<ResponseFavorite> getFavoriteResponse(Long id) {
        log.info("Fetching favorite response with ID: {}", id);
        Optional<Favorite> favorite = favoriteRepository.findById(id);
        return favorite.map(ResponseFavorite::new);
    }

    @Override
    public List<Favorite> getAllFavorites() {
        log.info("Fetching all favorites");
        return favoriteRepository.findAll();
    }

    @Override
    public List<ResponseFavorite> getAllFavoritesResponse() {
        log.info("Fetching all favorites as response");
        return favoriteRepository.findAll().stream()
                .map(ResponseFavorite::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<ResponseFavorite>> getFavoritesByArticleId(Long articleId) {
        log.info("Fetching favorites for article ID: {}", articleId);
        List<Favorite> favorites = favoriteRepository.findByArticleId(articleId);
        if (favorites.isEmpty()) {
            return Optional.empty();
        }
        List<ResponseFavorite> responseFavorites = favorites.stream()
                .map(ResponseFavorite::new)
                .collect(Collectors.toList());
        return Optional.of(responseFavorites);
    }

    @Override
    public Optional<List<ResponseFavorite>> getFavoritesByUserId() {
        User user = sharedService.getConnectedUser().orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Fetching favorites for user ID: {}", user.getId());
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
        if (favorites.isEmpty()) {
            return Optional.empty();
        }
        List<ResponseFavorite> responseFavorites = favorites.stream()
                .map(ResponseFavorite::new)
                .collect(Collectors.toList());
        return Optional.of(responseFavorites);
    }

    @Override
    public Optional<List<ArticleInfo>> getFavoriteArticlesByUser() {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Fetching favorite articles for user ID: {}", user.getId());
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());

        if (favorites.isEmpty()) {
            log.info("No favorite articles found for user ID: {}", user.getId());
            return Optional.empty();
        }

        /*
         * private Long id;
         * private String name;
         * private String categoryName;
         * private double price;
         * private Double averageRating;
         * private Long reviewCount;
         * private LocalDateTime addedToFavoritesAt;
         * private LocalDateTime articleCreatedAt;
         * private String imageUrl;
         */

        List<ArticleInfo> articleInfos = favorites.stream()
                .map(favorite -> new ArticleInfo(
                        favorite.getArticle().getId(),
                        favorite.getArticle().getName(),
                        favorite.getArticle().getCategory().getName(),
                        favorite.getArticle().getPrice(),
                        reviewRepository.findAverageRatingByArticleId(favorite.getArticle().getId()), // averageRating
                        reviewRepository.countByArticleId(favorite.getArticle().getId()), // reviewCount
                        favorite.getCreatedAt(), // addedToFavoritesAt
                        favorite.getArticle().getCreatedAt(), // articleCreatedAt
                        favorite.getArticle().getImage())) // imageUrl
                .collect(Collectors.toList());

        log.info("Found {} favorite articles for user ID: {}", articleInfos.size(), user.getId());
        return Optional.of(articleInfos);
    }

    @Override
    public int getFavoriteCountByUser() {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Fetching favorite count for user ID: {}", user.getId());
        int favoritesCount = favoriteRepository.countByUser(user);

        log.info("Found {} favorites for user ID: {}", favoritesCount, user.getId());
        return favoritesCount;
    }

    @Override
    public Optional<Boolean> isFavorite(Long articleId) {
        Optional<User> user = sharedService.getConnectedUser();
        if (user.isEmpty()) {
            log.error("User not found.");
            return Optional.of(false);
        }
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            log.error("Article not found with ID: {}", articleId);
            return Optional.of(false);
        }
        Optional<Favorite> favorite = favoriteRepository.findByUserAndArticle(user.get(), article.get());
        return Optional.of(favorite.isPresent());
    }

    public Optional<Boolean> _isFavorite(Article article, User user) {

        Optional<Favorite> favorite = favoriteRepository.findByUserAndArticle(user, article);
        return Optional.of(favorite.isPresent());
    }

    @Override
    public void deleteFavoriteByArticleId(Long articleId) {
        log.info("Deleting favorite for article ID: {}", articleId);

        Optional<User> user = sharedService.getConnectedUser();
        if (user.isEmpty()) {
            log.error("User not found.");
            throw new RuntimeException("User not found");
        }

        // Check if the favorite exists
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndArticleId(user.get(), articleId);
        if (existingFavorite.isEmpty()) {
            log.error("Favorite not found for article ID: {} and user ID: {}", articleId, user.get().getId());
            throw new RuntimeException("Favorite not found for this article");
        }

        favoriteRepository.deleteByArticleIdAndUser(articleId, user.get());
        log.info("Favorite deleted successfully for article ID: {} and user ID: {}", articleId, user.get().getId());
    }
}