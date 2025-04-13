package com.example.stage24.article.model.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class NewArticle{

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


    private String name;


    private String description;


    private double price;
    
    private long category;

    private int quantity;

    // private int categoryId;

    // private Category category;

    // private Stock stock;

    // @NotBlank
    // private String image;

}
