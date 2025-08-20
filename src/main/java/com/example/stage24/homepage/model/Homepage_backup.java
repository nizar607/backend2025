// package com.example.stage24.homepage.model;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import java.time.LocalDateTime;
// import java.util.List;

// @Entity
// @Table(name = "homepage")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class Homepage {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(name = "company_id", nullable = false)
//     private Long companyId;
    
//     // Hero Section
//     @Column(name = "hero_title")
//     private String heroTitle;
    
//     @Column(name = "hero_subtitle")
//     private String heroSubtitle;
    
//     @Column(name = "hero_primary_cta")
//     private String heroPrimaryCta;
    
//     @Column(name = "hero_secondary_cta")
//     private String heroSecondaryCta;
    
//     @Column(name = "hero_background_image", columnDefinition = "TEXT")
//     private String heroBackgroundImage;
    
//     @Column(name = "hero_badge")
//     private String heroBadge;
    
//     @Column(name = "hero_button_text")
//     private String heroButtonText;
    
//     @Column(name = "hero_primary_button")
//     private String heroPrimaryButton;
    
//     @Column(name = "hero_secondary_button")
//     private String heroSecondaryButton;
    
//     @Column(name = "hero_image", columnDefinition = "TEXT")
//     private String heroImage;
    
//     // Featured Section
//     @Column(name = "featured_title")
//     private String featuredTitle;
    
//     @Column(name = "featured_subtitle")
//     private String featuredSubtitle;
    
//     // Categories Section
//     @Column(name = "categories_title")
//     private String categoriesTitle;
    
//     @Column(name = "categories_subtitle")
//     private String categoriesSubtitle;
    
//     // Experience Section
//     @Column(name = "experience_title")
//     private String experienceTitle;
    
//     @Column(name = "experience_subtitle")
//     private String experienceSubtitle;
    
//     // Gallery Section
//     @Column(name = "gallery_title")
//     private String galleryTitle;
    
//     @Column(name = "gallery_subtitle")
//     private String gallerySubtitle;
    
//     // Features Section
//     @Column(name = "features_title")
//     private String featuresTitle;
    
//     @Column(name = "features_subtitle")
//     private String featuresSubtitle;
    
//     // Products Section
//     @Column(name = "products_title")
//     private String productsTitle;
    
//     // Why Choose Section
//     @Column(name = "why_choose_title")
//     private String whyChooseTitle;
    
//     // Artisan Section
//     @Column(name = "artisan_title")
//     private String artisanTitle;
    
//     @Column(name = "artisan_description", columnDefinition = "TEXT")
//     private String artisanDescription;
    
//     @Column(name = "artisan_link")
//     private String artisanLink;
    
//     @Column(name = "artisan_image", columnDefinition = "TEXT")
//     private String artisanImage;
    
//     // Newsletter Section
//     @Column(name = "newsletter_title")
//     private String newsletterTitle;
    
//     @Column(name = "newsletter_text", columnDefinition = "TEXT")
//     private String newsletterText;
    
//     @Column(name = "is_active")
//     private Boolean isActive = true;
    
//     @CreationTimestamp
//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;
    
//     @UpdateTimestamp
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
    
//     // One-to-Many relationships for nested entities
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<FeaturedProduct> featuredProducts;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<CategoryItem> categoryItems;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<ExperienceCard> experienceCards;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<GalleryProduct> galleryProducts;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<FeatureItem> featureItems;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<ProductItem> productItems;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<HomepageStatistic> statistics;
    
//     @OneToMany(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private List<WhyChooseFeature> whyChooseFeatures;
    
//     @OneToOne(mappedBy = "homepage", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     private DiningInfo diningInfo;
// }