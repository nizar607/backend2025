package com.example.stage24.article.domain;

import com.example.stage24.article.domain.Article;
import com.example.stage24.company.model.Company;
import com.example.stage24.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "categories")
public class Category {

    /*
     * - id: int
     * - name: String
     * - description: String
     * - createdAt: Date
     * - updatedAt: Date
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category",fetch = FetchType.EAGER)
    private List<Article> articles = new LinkedList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();
}
