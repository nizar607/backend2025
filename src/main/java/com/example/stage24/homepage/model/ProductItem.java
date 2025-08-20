package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    
    @Column(name = "badge")
    private String badge;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "price")
    private String price;
    
    @Column(name = "button_text")
    private String buttonText;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}