package com.example.stage24.basket.service.implementation;


import com.example.stage24.article.domain.Article;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.response.ResponseArticle;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.article.service.interfaces.ArticleServiceInterface;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ArticleServiceImplementation implements ArticleServiceInterface {

    private ArticleRepository articleRepository;
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
        savedArticle.setQuantity(article.getQuantity());

        Article result = articleRepository.save(savedArticle);
        articlesList.add(result);
        category.setArticles(articlesList);
        categoryRepository.save(category);

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
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        return Optional.of(new ResponseArticle(article));
    }

    @Override
    public Optional<List<ResponseArticle>> getArticleByNameResponse(String name) {
        List<Article> articles = articleRepository.findArticleByNameLike("%" + name + "%").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article);
            return responseArticle;
        }).toList();
        return Optional.of(articleResponses);
    }

    @Override
    public Optional<List<ResponseArticle>> searchArticle(String name, double minPrice, double maxPrice, List<Integer> categories) {
        List<Article> articles = articleRepository.findArticleByNameLikeAndPriceBetweenAndCategoryIdIn("%" + name + "%",minPrice,maxPrice,categories).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        List<ResponseArticle> articleResponses = articles.stream().map((article) -> {
            ResponseArticle responseArticle = new ResponseArticle(article);
            return responseArticle;
        }).toList();
        return Optional.of(articleResponses);
    }

    @Override
    public Optional<List<ResponseArticle>> getArticleByPriceResponse(double minPrice, double maxPrice) {
        List<Article> articles = articleRepository.findArticleByPriceBetween(minPrice,maxPrice).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
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


}

