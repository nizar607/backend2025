package com.example.stage24.basket.repository;

import com.example.stage24.article.domain.Article;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.stage24.article.domain.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    public Optional<List<Article>> findArticleByName(String name);

    public Optional<List<Article>> findArticleByNameLike(String name);

    public Optional<List<Article>> findArticleByCategoryId(Long id);

    public Optional<List<Article>> findArticleByPriceBetween(Double lower, Double higher);

    Optional<List<Article>> findArticleByNameLikeAndPriceBetweenAndCategoryIdIn(String name, double priceAfter, double priceBefore, Collection<Integer> categoryIds);
}