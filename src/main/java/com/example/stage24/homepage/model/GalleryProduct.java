package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gallery_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "price")
    private String price;
    
    @Column(name = "button")
    private String button;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}