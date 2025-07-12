package com.example.stage24.article.service.implementation;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.response.ResponseArticle;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.basket.repository.ItemRepository;
import com.example.stage24.favorite.repository.FavoriteRepository;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import com.example.stage24.notification.domain.Notification;
import com.example.stage24.notification.service.NotificationService;
import com.example.stage24.review.repository.ReviewRepository;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ArticleServiceImplementation implements ArticleServiceInterface {

    private ArticleRepository articleRepository;
    private CategoryRepository categoryRepository;
    private CategoryServiceInterface categoryService;
    private SharedServiceInterface sharedService;
    private FavoriteRepository favoriteRepository;
    private NotificationService notificationService;
    private ReviewRepository reviewRepository;
    private ItemRepository itemRepository;

    @Override
    public Article addArticle(NewArticle article) {
        log.info("Adding article: " + article);
        Article savedArticle = new Article();
        savedArticle.setName(article.getName());
        savedArticle.setDescription(article.getDescription());
        savedArticle.setPrice(article.getPrice());
        savedArticle.setImage(article.getImage());

        Category category = categoryService.getCategory(article.getCategory())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        List<Article> articlesList = category.getArticles();
        if (articlesList == null) {
            articlesList = new LinkedList<>();
        }

        savedArticle.setCategory(category);
        savedArticle.setQuantity(article.getQuantity());

        Article result = articleRepository.save(savedArticle);
        articlesList.add(result);
        category.setArticles(articlesList);
        categoryRepository.save(category);

        // Send notification to all users about the new article
        try {
            Notification notification = new Notification(
                    "article", // type
                    "New article added: " + result.getName() + " in category " + category.getName(), // message
                    new Date(), // timestamp
                    false // read status
            );
            notificationService.sendBroadcastNotification(notification);
            log.info("Notification sent for new article: {}", result.getName());
        } catch (Exception e) {
            // Log the error but don't prevent article creation
            log.error("Failed to send notification for new article: {}", e.getMessage(), e);
        }

        return result;
    }

    @Override
    public ResponseArticle updateArticle(Article article) {
        Article savedArticle = articleRepository.save(article);
        return new ResponseArticle(savedArticle);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public Optional<Article> getArticle(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Optional<ResponseArticle> getArticleResponse(Long id) {
        double averageRating = reviewRepository.getAverageRatingByArticleId(id).orElse(0.0);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        return Optional.of(new ResponseArticle(article, averageRating));
    }

    @Override
    public Optional<List<ResponseArticle>> getArticleByNameResponse(String name) {
        List<Article> articles = articleRepository.findArticleByNameLike("%" + name + "%")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article);
            return responseArticle;
        }).toList();
        return Optional.of(articleResponses);
    }

    @Override
    public Optional<List<ResponseArticle>> searchArticle(String name, double minPrice, double maxPrice,
            List<Integer> categories) {
        List<Article> articles = articleRepository
                .findArticleByNameLikeAndPriceBetweenAndCategoryIdIn("%" + name + "%", minPrice, maxPrice, categories)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article);
            return responseArticle;
        }).toList();
        return Optional.of(articleResponses);
    }

    @Override
    public Optional<List<ResponseArticle>> getArticleByPriceResponse(double minPrice, double maxPrice) {
        List<Article> articles = articleRepository.findArticleByPriceBetween(minPrice, maxPrice)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article);
            return responseArticle;
        }).toList();
        return Optional.of(articleResponses);
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public List<ResponseArticle> getAllArticlesResponse() {
        return articleRepository.findAll().stream().map(ResponseArticle::new).collect(Collectors.toList());
    }

    @Override
    public Optional<List<ResponseArticle>> getFavoriteArticlesByUserId() {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Article> articles = articleRepository.findArticlesByUserFavorites(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article, true); // These are all favorites
            return responseArticle;
        }).toList();
        return Optional.of(articleResponses);
    }

    // Helper method to check if an article is favorited by a user
    private boolean isArticleFavorited(Long articleId) {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return favoriteRepository.findByUserAndArticleId(user, articleId).isPresent();
    }

    // Helper method to check if an article is in user's cart
    private boolean isArticleInCart(Long articleId, Set<Long> cartArticleIds) {
        return cartArticleIds.contains(articleId);
    }

    @Override
    public Optional<ResponseArticle> getArticleResponseWithFavorite(Long id) {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Set<Long> cartArticleIds = itemRepository.findArticleIdsByUserId(user.getId());
        
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        boolean isFavorite = isArticleFavorited(id);
        boolean isInCart = isArticleInCart(id, cartArticleIds);
        return Optional.of(new ResponseArticle(article, isFavorite, isInCart));
    }

    @Override
    public List<ResponseArticle> getAllArticlesResponseWithFavorite() {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Set<Long> cartArticleIds = itemRepository.findArticleIdsByUserId(user.getId());
        
        return articleRepository.findAll().stream()
                .map(article -> new ResponseArticle(article, 
                    isArticleFavorited(article.getId()),
                    isArticleInCart(article.getId(), cartArticleIds)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<ResponseArticle>> getArticleByNameResponseWithFavorite(String name) {
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Set<Long> cartArticleIds = itemRepository.findArticleIdsByUserId(user.getId());
        
        List<Article> articles = articleRepository.findArticleByNameLike("%" + name + "%")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream()
                .map(article -> new ResponseArticle(article, 
                    isArticleFavorited(article.getId()),
                    isArticleInCart(article.getId(), cartArticleIds)))
                .toList();
        return Optional.of(articleResponses);
    }

    @Override
    public Optional<List<ResponseArticle>> searchArticleWithFavorite(String name, double minPrice, double maxPrice,
            List<Integer> categories) {
        List<Article> articles = articleRepository
                .findArticleByNameLikeAndPriceBetweenAndCategoryIdIn("%" + name + "%", minPrice, maxPrice, categories)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream()
                .map(article -> new ResponseArticle(article, isArticleFavorited(article.getId())))
                .toList();
        return Optional.of(articleResponses);
    }

}
