package com.example.stage24.favorite.controller;

import com.example.stage24.favorite.domain.Favorite;
import com.example.stage24.favorite.model.request.NewFavorite;
import com.example.stage24.favorite.model.response.ArticleInfo;
import com.example.stage24.favorite.model.response.ResponseFavorite;
import com.example.stage24.favorite.service.interfaces.FavoriteServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteServiceInterface favoriteService;
    private final SharedServiceInterface sharedService;

    // Create new favorite
    @PostMapping
    public ResponseEntity<ResponseFavorite> createFavorite(@Valid @RequestBody NewFavorite favorite) {
        log.info("Creating new favorite with article ID: {}", favorite.getArticleId());
        try {
            Favorite savedFavorite = favoriteService.addFavorite(favorite);
            ResponseFavorite responseFavorite = new ResponseFavorite(savedFavorite);
            return new ResponseEntity<>(responseFavorite, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating favorite: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update favorite
    @PutMapping("/{id}")
    public ResponseEntity<ResponseFavorite> updateFavorite(@PathVariable("id") Long id,
            @Valid @RequestBody NewFavorite favorite) {
        log.info("Updating favorite with ID: {} with article ID: {}", id, favorite.getArticleId());
        try {
            Favorite updatedFavorite = favoriteService.updateFavorite(id, favorite);
            ResponseFavorite responseFavorite = new ResponseFavorite(updatedFavorite);
            return new ResponseEntity<>(responseFavorite, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating favorite: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Get all favorites
    @GetMapping
    public ResponseEntity<List<ResponseFavorite>> getAllFavorites() {
        log.info("Fetching all favorites");
        List<ResponseFavorite> favorites = favoriteService.getAllFavoritesResponse();
        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }

    // Get favorite by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseFavorite> getFavoriteById(@PathVariable("id") Long id) {
        log.info("Fetching favorite with ID: {}", id);
        Optional<ResponseFavorite> favorite = favoriteService.getFavoriteResponse(id);
        return favorite.map(responseFavorite -> new ResponseEntity<>(responseFavorite, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get favorites by article ID
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<ResponseFavorite>> getFavoritesByArticleId(@PathVariable("articleId") Long articleId) {
        log.info("Fetching favorites for article ID: {}", articleId);
        Optional<List<ResponseFavorite>> favorites = favoriteService.getFavoritesByArticleId(articleId);
        return favorites.map(responseFavorites -> new ResponseEntity<>(responseFavorites, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get favorites by user ID
    @GetMapping("/user/favorites/count")
    public ResponseEntity<Integer> getFavoritesCount() {
        try {
            int favoritesCount = favoriteService.getFavoriteCountByUser();
            return ResponseEntity.ok(favoritesCount);
            
        } catch (RuntimeException e) {
            log.error("Error fetching favorites count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Get favorite articles by user ID
    @GetMapping("/user/articles")
    public ResponseEntity<List<ArticleInfo>> getFavoriteArticlesByUser() {
        try {
            Optional<List<ArticleInfo>> articles = favoriteService.getFavoriteArticlesByUser();
            return articles.map(articleInfos -> ResponseEntity.ok(articleInfos))
                    .orElse(ResponseEntity.ok(Collections.emptyList()));
        } catch (RuntimeException e) {
            log.error("Error fetching favorite articles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Delete favorite by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFavorite(@PathVariable("id") Long id) {
        log.info("Deleting favorite with ID: {}", id);
        try {
            favoriteService.deleteFavorite(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting favorite: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete favorite by article ID
    @DeleteMapping("/article/{articleId}")
    public ResponseEntity<HttpStatus> deleteFavoriteByArticleId(@PathVariable("articleId") Long articleId) {
        log.info("Deleting favorite for article ID: {}", articleId);
        try {
            favoriteService.deleteFavoriteByArticleId(articleId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting favorite for article ID {}: {}", articleId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/is-favorite/{articleId}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable("articleId") Long articleId) {
        log.info("Checking if article with ID: {} is favorite", articleId);
        Optional<User> user = sharedService.getConnectedUser();
        if (user.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        boolean isFavorite = favoriteService.isFavorite(articleId).isPresent();
        return new ResponseEntity<>(isFavorite, HttpStatus.OK);
    }
}
