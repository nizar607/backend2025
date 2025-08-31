package com.example.stage24.article.repository;

import com.example.stage24.article.domain.Article;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.stage24.article.domain.Category;
import com.example.stage24.company.model.Company;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    public Optional<List<Article>> findArticleByName(String name);

    public Optional<List<Article>> findArticleByNameLike(String name);

    public Optional<List<Article>> findArticleByCategoryId(Long id);

    public Optional<List<Article>> findArticleByPriceBetween(Double lower, Double higher);

    public Optional<List<Article>> findArticleByNameLikeAndPriceBetweenAndCategoryIdIn(String name, double priceAfter,
            double priceBefore, Collection<Integer> categoryIds);

    @Query("SELECT a FROM Article a JOIN Favorite f ON a.id = f.article.id WHERE f.user.id = :userId ORDER BY f.addedAt DESC")
    public Optional<List<Article>> findArticlesByUserFavorites(@Param("userId") Long userId);

    /**
     * Find articles by company
     */
    public Optional<List<Article>> findByCompany(Company company);

    /**
     * Find articles by company ID
     */
    @Query("SELECT a FROM Article a WHERE a.company.id = :companyId")
    public Optional<List<Article>> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * Find articles by name, price range, categories, and company
     */
    @Query("SELECT a FROM Article a " +
            "WHERE a.name LIKE CONCAT('%', :name, '%') " +
            "AND a.price BETWEEN :minPrice AND :maxPrice " +
            "AND a.category.id IN (:categoryIds) " +
            "AND a.company = :company")
    Optional<List<Article>> findArticleByNameLikeAndPriceBetweenAndCategoryIdInAndCompany(
            @Param("name") String name,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice,
            @Param("categoryIds") Collection<Integer> categoryIds,
            @Param("company") Company company);
}