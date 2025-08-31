package com.example.stage24.article.domain;



import com.example.stage24.company.model.Company;
import com.example.stage24.user.domain.Role;
import com.example.stage24.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "articles")
public class Article {

    /*
     * - id: long
     * - name: string
     * - description: string
     * - price: double
     * - stockId: int
     * - categoryId: int
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

    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private int quantity;

    private String image;

    private String sku; // Stock Keeping Unit for inventory tracking

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

}
