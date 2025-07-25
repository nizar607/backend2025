package com.example.stage24.basket.controller;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.response.ResponseArticle;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/shopingcart")
public class ShopingCartController {

    private final ArticleServiceInterface articleService;
    private final SharedServiceInterface sharedService;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;


    // Create new article
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestParam("name") String name,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("price") double price,
                                                 @RequestParam("quantity") int quantity,
                                                 @RequestParam("category") int category,
                                                 @RequestParam(value = "file", required = false) MultipartFile image) {
        NewArticle article = new NewArticle();
        article.setName(name);
        article.setDescription(description);
        article.setPrice(price);
        article.setCategory(category);
        article.setQuantity(quantity);
        String filename = fileStorageService.store(image);
        article.setImage(filename);

        log.info("Creating new article: {}", article);
        Article savedArticle = articleService.addArticle(article);
        return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
    }

    // Update article
    @PutMapping("/{id}")
    public ResponseEntity<ResponseArticle> updateArticle(@PathVariable("id") Long id,
                                                         @RequestParam("name") String name,
                                                         @RequestParam("description") String description,
                                                         @RequestParam("price") double price,
                                                         @RequestParam("quantity") int quantity,
                                                         @RequestParam("category") long category,
                                                         @RequestParam(value = "file", required = false) MultipartFile image) {

        Category _category = this.categoryRepository.findById(category).orElseThrow(() -> new RuntimeException("Category not found"));
        Article _article = articleService.getArticle(id).orElseThrow(() -> new RuntimeException("Category not found"));
        _article.setCategory(_category);
        _article.setName(name);
        _article.setDescription(description);
        _article.setPrice(price);
        _article.setQuantity(quantity);
        if (image != null) {
            String filename = fileStorageService.store(image);
            _article.setImage(filename);
        }
        _article.setUpdatedAt(LocalDateTime.now());

        ResponseArticle article = articleService.updateArticle(_article);

        return new ResponseEntity<>(article, HttpStatus.OK);
    }


    // Get all articles
    @GetMapping
    public ResponseEntity<List<ResponseArticle>> getAllArticles() {
        List<ResponseArticle> articles = articleService.getAllArticlesResponse();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    // Get article by id
    @GetMapping("/search")
    public ResponseEntity<List<ResponseArticle>> searchArticleByName(@RequestParam("searchValue") String name,
                                                                     @RequestParam("minPrice") double minPrice,
                                                                     @RequestParam("maxPrice") double maxPrice,
                                                                     @RequestParam("categories") List<Integer> categories) {
        return articleService.searchArticle(name,minPrice,maxPrice,categories)
                .map(article -> new ResponseEntity<>(article, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/searchByPrice/{minPrice}/{maxPrice}")
    public ResponseEntity<List<ResponseArticle>> searchArticleByPrice(@PathVariable("minPrice") double minPrice,
                                                                      @PathVariable("maxPrice") double maxPrice) {
        return articleService.getArticleByPriceResponse(minPrice, maxPrice)
                .map(article -> new ResponseEntity<>(article, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get article by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseArticle> getArticleById(@PathVariable("id") Long id) {
        return articleService.getArticleResponse(id)
                .map(article -> new ResponseEntity<>(article, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // Delete article
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArticle(@PathVariable("id") Long id) {
        return articleService.getArticle(id)
                .map(article -> {
                    articleService.deleteArticle(id);
                    return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
