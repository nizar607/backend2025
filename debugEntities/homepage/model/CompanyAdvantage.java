package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "company_advantages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAdvantage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homepage_content_id")
    @JsonBackReference
    private HomepageContent homepageContent;
    
    @Column(name = "advantage_text", nullable = false)
    private String advantageText;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "icon")
    private String icon; // Optional icon class or image URL
    
    @Column(name = "description")
    private String description; // Optional detailed description
}