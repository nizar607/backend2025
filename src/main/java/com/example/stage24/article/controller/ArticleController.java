package com.example.stage24.article.controller;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.domain.Stock;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.repository.StockRepository;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleServiceInterface articleService;
    private final SharedServiceInterface sharedService;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
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
    public ResponseEntity<Article> updateArticle(@PathVariable("id") Long id,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("price") double price,
                                                 @RequestParam("quantity") int quantity,
                                                 @RequestParam("category") long category,
                                                 @RequestParam(value = "file", required = false) MultipartFile image) {

        Category _category = this.categoryRepository.findById((long) 1).orElseThrow(() -> new RuntimeException("Category not found jemla"));
        Article article = articleService.getArticle(id).orElseThrow(() -> new RuntimeException("Category not found"));
        Stock stock = stockRepository.findById(article.getStock().getId()).orElseThrow(() -> new RuntimeException("Stock not found"));
        stock.setQuantity(quantity);
        stockRepository.save(stock);

        return articleService.getArticle(id)
                .map(existingArticle -> {
                    existingArticle.setName(name);
                    existingArticle.setDescription(description);
                    existingArticle.setPrice(price);
                    existingArticle.setCategory(_category);
                    existingArticle.setStock(stock);
                    if(image != null) {
                        String filename = fileStorageService.store(image);
                        existingArticle.setImage(filename);
                    }
                    existingArticle.setUpdatedAt(LocalDateTime.now());
                    return new ResponseEntity<>(articleRepository.save(existingArticle), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }


    // Get all articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    // Get article by id
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable("id") Long id) {
        return articleService.getArticle(id)
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
