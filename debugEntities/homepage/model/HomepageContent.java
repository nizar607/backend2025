package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.stage24.company.model.Company;

import java.util.List;

@Entity
@Table(name = "homepage_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @OneToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;
    
    // Section 1: Hero Section
    @Column(name = "hero_title", nullable = false)
    private String heroTitle;
    
    @Column(name = "hero_tag")
    private String heroTag; // Optional
    
    @Column(name = "hero_description", columnDefinition = "TEXT")
    private String heroDescription;
    
    @Column(name = "hero_image")
    private String heroImage;
    
    @OneToMany(mappedBy = "homepageContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<HomepageProduct> heroProducts;
    
    // Section 2: Company Info Section
    @Column(name = "section_two_title")
    private String sectionTwoTitle;
    
    @Column(name = "section_two_description", columnDefinition = "TEXT")
    private String sectionTwoDescription;
    
    @OneToOne(mappedBy = "homepageContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private CompanyInfo companyInfo;
    
    @OneToMany(mappedBy = "homepageContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CompanyAdvantage> advantages;
    
    // Section 3: Artisan Spotlight Section
    @Column(name = "section_three_title")
    private String sectionThreeTitle;
    
    @Column(name = "section_three_description", columnDefinition = "TEXT")
    private String sectionThreeDescription;
    
    @Column(name = "section_three_cta_text")
    private String sectionThreeCtaText; // e.g., "Meet the Team →"
    
    @Column(name = "section_three_cta_link")
    private String sectionThreeCtaLink;
    
    @OneToMany(mappedBy = "homepageContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<HomepageProduct> spotlightProducts;
    
    @OneToOne(mappedBy = "homepageContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private ArtisanInfo artisanInfo;
}