package com.example.stage24.basket.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewArticle{

    private String name;

    private String description;

    private double price;
    
    private long category;

    private int quantity;

    private String image;

}
