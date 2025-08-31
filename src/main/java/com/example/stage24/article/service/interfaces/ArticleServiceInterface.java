package com.example.stage24.article.service.interfaces;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.model.request.NewArticle;
import com.example.stage24.article.model.response.ResponseArticle;

import java.io.IOException;
import java.util.Optional;
import java.util.List;

public interface ArticleServiceInterface {

    public Article addArticle(NewArticle article);

    public ResponseArticle updateArticle(Article article);

    public void deleteArticle(Long id);

    public Optional<Article> getArticle(Long id);

    public Optional<ResponseArticle> getArticleResponse(Long id);

    public List<Article> getAllArticles();

    public List<ResponseArticle> getAllArticlesResponse();

    public Optional<List<ResponseArticle>> getArticleByNameResponse(String name);

    public Optional<List<ResponseArticle>> getArticleByPriceResponse(double minPrice, double maxPrice);

    public Optional<List<ResponseArticle>> searchArticle(String name, double minPrice, double maxPrice,
            List<Integer> categories);


    public Optional<List<ResponseArticle>> searchArticleByCompany(String name, double minPrice, double maxPrice,
            List<Integer> categories, String website);

    public Optional<List<ResponseArticle>> getFavoriteArticlesByUserId();

    // Methods with user context for favorite checking
    public Optional<ResponseArticle> getArticleResponseWithFavorite(Long id);

    public List<ResponseArticle> getAllArticlesResponseWithFavorite();

    public Optional<List<ResponseArticle>> getArticleByNameResponseWithFavorite(String name);

    public Optional<List<ResponseArticle>> searchArticleWithFavorite(String name, double minPrice, double maxPrice,
            List<Integer> categories);

    /**
     * Get articles by website (company)
     */
    public Optional<List<ResponseArticle>> getArticlesByWebsite(String website);

    /**
     * Get article details by ID without user-specific data (for public access)
     */
    public Optional<ResponseArticle> getArticleDetails(Long id);

}
