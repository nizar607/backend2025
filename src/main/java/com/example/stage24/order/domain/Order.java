package com.example.stage24.order.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.*;

@Data
public class Order {

    /*
     * - id: int
     * - userID: int
     * - totalAmount: double
     * - paymentMethod: String
     * - address: String
     * - createdAt: LocalDateTime
     * - updatedAt: LocalDateTime
     */

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @NotNull
    private int userId;

    @NotNull
    private double totalAmount;

    @NotNull
    private String paymentMethod;

    @NotNull
    private String address;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

}
