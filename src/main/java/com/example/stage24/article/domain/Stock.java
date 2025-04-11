/*
 * - id: int
 * - articleID: int
 * - quantity: int
 * - createdAt: LocalDateTime
 * - updatedAt: LocalDateTime
 */
package com.example.stage24.article.domain;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.example.stage24.user.domain.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int quantity;

    private int minQuantity;

    private int maxQuantity;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

}
