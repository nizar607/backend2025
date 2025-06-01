package com.example.stage24.article.controller;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.request.NewCategory;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryServiceInterface categoryService;


    // Create new article

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody NewCategory category) {
        log.info("Creating new category: {}", category);

        Category savedCategory = categoryService.addCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }


    // Get all articles
    @GetMapping
    public ResponseEntity<List<Category>> getAllArticles() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
/*
    // Get article by id
    @GetMapping("/{id}")
    public ResponseEntity<Category> getArticleById(@PathVariable("id") Long id) {
        return categoryService.getCategory(id)
                .map(category -> new ResponseEntity<>(category, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
*/

    // Update article
    /*
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable("id") Long id, @Valid @RequestBody Article article) {

        return articleService.getArticle(id)
                .map(existingArticle -> {
                    existingArticle.setName(article.getName());
                    existingArticle.setDescription(article.getDescription());
                    existingArticle.setPrice(article.getPrice());
                    existingArticle.setCategory(article.getCategory());
                    existingArticle.setStock(article.getStock());
                    existingArticle.setUpdatedAt(LocalDateTime.now());

                    //Article updatedArticle = articleService.updateArticle(existingArticle);
                    //return new ResponseEntity<>(updatedArticle, HttpStatus.OK);
                    return new ResponseEntity<>(new Article(), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    */


    // Delete article
    /*
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArticle(@PathVariable("id") Long id) {
        return articleService.getArticle(id)
                .map(article -> {
                    articleService.deleteArticle(id);
                    return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
     */
}
