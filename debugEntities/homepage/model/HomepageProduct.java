package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "homepage_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homepage_content_id")
    @JsonBackReference
    private HomepageContent homepageContent;
    
    @Column(name = "product_id")
    private Long productId; // Reference to actual product
    
    @Column(name = "section_type")
    @Enumerated(EnumType.STRING)
    private SectionType sectionType; // HERO, SPOTLIGHT
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "featured")
    private Boolean featured = false;
    
    public enum SectionType {
        HERO,
        SPOTLIGHT
    }
}