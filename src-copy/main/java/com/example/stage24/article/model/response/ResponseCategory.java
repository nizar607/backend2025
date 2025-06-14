package com.example.stage24.article.model.response;
import com.example.stage24.article.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseCategory {

    private Long id;

    private String name;

    private String description;

    private int articles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt ;

    public ResponseCategory(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.articles = category.getArticles().size();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }
    public ResponseCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
