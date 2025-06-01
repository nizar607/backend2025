package com.example.stage24.article.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseArticle {

        private String name;

        private String description;

        private double price;

        private long category;

        private int quantity;

        private String image;


}
