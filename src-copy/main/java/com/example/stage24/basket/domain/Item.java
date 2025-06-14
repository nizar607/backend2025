package com.example.stage24.basket.domain;

import com.example.stage24.article.domain.Article;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {

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
    @ManyToOne(fetch = FetchType.EAGER)
    private ShopingCart shopingCart;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();
}
