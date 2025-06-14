package com.example.stage24.article.controller;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.request.NewCategory;
import com.example.stage24.article.model.response.ResponseCategory;
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
    public ResponseEntity<ResponseCategory> createCategory(@Valid @RequestBody NewCategory category) {
        log.info("Creating new category: {}", category);

        Category savedCategory = categoryService.addCategory(category);
        ResponseCategory responseCategory = new ResponseCategory(savedCategory);
        return new ResponseEntity<>(responseCategory, HttpStatus.CREATED);
    }

    // Update article
    @PutMapping("/{id}")
    public ResponseEntity<ResponseCategory> updateCategory(@PathVariable("id") Long id,@RequestBody ResponseCategory category) {
        Category _category = categoryService.updateCategory(id,category);
        return new ResponseEntity<>(new ResponseCategory(_category), HttpStatus.OK);
    }



    // Get all articles
    @GetMapping
    public ResponseEntity<List<ResponseCategory>> getAllArticles() {
        List<ResponseCategory> categories = categoryService.getAllCategories().stream().map((category) -> new ResponseCategory(category)).toList();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Delete article

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
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
}
