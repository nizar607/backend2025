package com.example.stage24.favorite.model.response;

import com.example.stage24.article.domain.Article;
import com.example.stage24.favorite.domain.Favorite;
import com.example.stage24.review.repository.ReviewRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseFavorite {

    private Long id;

    private Long userId;

    private String userName;

    private ArticleInfo article;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private static ReviewRepository reviewRepository;
    
    @Autowired
    public void setReviewRepository(ReviewRepository reviewRepository) {
        ResponseFavorite.reviewRepository = reviewRepository;
    }

    public ResponseFavorite(Favorite favorite) {
        this.id = favorite.getId();
        this.userId = favorite.getUser().getId();
        this.userName = favorite.getUser().getFirstName() + " " + favorite.getUser().getLastName();
        
        Article articleEntity = favorite.getArticle();
        if (articleEntity != null) {
            this.article = new ArticleInfo(
                articleEntity.getId(), 
                articleEntity.getName(),
                articleEntity.getCategory().getName(),
                articleEntity.getPrice(),
                getAverageRating(articleEntity.getId()),
                getReviewCount(articleEntity.getId()),
                favorite.getAddedAt(),
                articleEntity.getCreatedAt(),
                articleEntity.getImage()
            );
        }
        
        this.createdAt = favorite.getCreatedAt();
        this.updatedAt = favorite.getUpdatedAt();
    }
    
    private Double getAverageRating(Long articleId) {
        if (reviewRepository != null) {
            return reviewRepository.findAverageRatingByArticleId(articleId);
        }
        return 0.0; // Default if repository is not available
    }
    
    private Long getReviewCount(Long articleId) {
        if (reviewRepository != null) {
            return reviewRepository.countByArticleId(articleId);
        }
        return 0L; // Default if repository is not available
    }


}