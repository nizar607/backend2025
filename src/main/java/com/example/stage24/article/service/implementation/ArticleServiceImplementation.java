package com.example.stage24.article.service.implementation;


import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.domain.Stock;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.repository.StockRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class ArticleServiceImplementation implements ArticleServiceInterface {

    private ArticleRepository articleRepository;
    private StockRepository stockRepository;
    private CategoryRepository categoryRepository;
    private CategoryServiceInterface categoryService;
    private SharedServiceInterface sharedService;

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
        Stock stock = new Stock();
        stock.setQuantity(article.getQuantity());
        savedArticle.setStock(stock);

        Article result = articleRepository.save(savedArticle);
        stock.setArticle(savedArticle);
        stockRepository.save(stock);
        articlesList.add(result);
        category.setArticles(articlesList);
        categoryRepository.save(category);

        return result;
    }

    @Override
    public Article updateArticle(Article article) {
        return articleRepository.save(article);
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
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }


}

