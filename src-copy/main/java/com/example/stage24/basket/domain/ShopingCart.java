package com.example.stage24.basket.domain;


import com.example.stage24.article.domain.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "shopingCarts")
public class ShopingCart {

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

    private double subTotalAmount;

    private double taxAmount;

    private double totalAmount;

    @OneToMany(mappedBy = "shopingCart",fetch = FetchType.EAGER)
    private List<Item> item;


    private int quantity;

    private String image;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

}
