package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "count")
    private String count;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}