package com.example.stage24.order.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.*;

@Data
public class OrderItem {

    /*
     * - id: int
     * - articleId: int
     * - amount: double
     * - quantity: int
     * - createdAt: Date
     * - updatedAt: Date
     */

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @NotNull
    private int articleId;

    @NotNull
    private double amount;

    @NotNull
    private int quantity;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();
    
}
