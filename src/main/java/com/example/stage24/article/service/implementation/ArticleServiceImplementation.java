package com.example.stage24.article.service.implementation;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.response.ResponseArticle;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.basket.repository.ItemRepository;
import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
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

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryServiceInterface categoryService;
    private final SharedServiceInterface sharedService;
    private final FavoriteRepository favoriteRepository;
    private final NotificationService notificationService;
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final CompanyRepository companyRepository;



    @Override
    public Article addArticle(NewArticle article) {
        log.info("Adding article: " + article);
        
        // Get the current user's company
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Company company = user.getCompany();
        if (company == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must be associated with a company to add articles");
        }
        
        Article savedArticle = new Article();
        savedArticle.setName(article.getName());
        savedArticle.setDescription(article.getDescription());
        savedArticle.setPrice(article.getPrice());
        savedArticle.setImage(article.getImage());
        savedArticle.setCompany(company); // Associate article with company

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
    public Optional<List<ResponseArticle>> searchArticleByCompany(String name, double minPrice, double maxPrice,
            List<Integer> categories, String website) {
        log.info("Searching articles for website: {} with criteria: name={}, minPrice={}, maxPrice={}, categories={}", 
                website, name, minPrice, maxPrice, categories);
        
        // Find company by website
        Company company = companyRepository.findByWebsite(website)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found for website: " + website));
        
        // Search articles by company and other criteria
        List<Article> articles = articleRepository
                .findArticleByNameLikeAndPriceBetweenAndCategoryIdInAndCompany("%" + name + "%", minPrice, maxPrice, categories, company)
                .orElse(List.of());
                
        
        if (articles.isEmpty()) {
            log.info("No articles found for website: {} with given criteria", website);
            return Optional.of(List.of());
        }
        
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article);
            return responseArticle;
        }).toList();
        
        log.info("Found {} articles for website: {} with given criteria", articleResponses.size(), website);
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
    public Optional<ResponseArticle> getArticleDetails(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        // Return article without user-specific data for public access
        return Optional.of(new ResponseArticle(article, false, false));
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



    @Override
    public Optional<List<ResponseArticle>> getArticlesByWebsite(String website) {
        log.info("Fetching articles for website: {}", website);
        
        // Find company by website
        Company company = companyRepository.findByWebsite(website)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found for website: " + website));
        
        // Find articles by company
        List<Article> articles = articleRepository.findByCompany(company)
                .orElse(List.of());
        
        if (articles.isEmpty()) {
            log.info("No articles found for website: {}", website);
            return Optional.of(List.of());
        }
        
        List<ResponseArticle> articleResponses = articles.stream()
                .map(ResponseArticle::new)
                .collect(Collectors.toList());
        
        log.info("Found {} articles for website: {}", articleResponses.size(), website);
        return Optional.of(articleResponses);
    }

}
