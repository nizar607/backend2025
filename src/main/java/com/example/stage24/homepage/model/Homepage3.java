package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.stage24.company.model.Company;

import java.time.LocalDateTime;

@Entity
@Table(name = "homepage3")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Homepage3 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    // Hero Section
    @Column(name = "hero_title")
    private String heroTitle;
    
    @Column(name = "hero_subtitle")
    private String heroSubtitle;
    
    @Column(name = "hero_primary_cta")
    private String heroPrimaryCta;
    
    @Column(name = "hero_secondary_cta")
    private String heroSecondaryCta;
    
    @Column(name = "hero_background_image", columnDefinition = "TEXT")
    private String heroBackgroundImage;
    
    @Column(name = "hero_badge")
    private String heroBadge;
    
    @Column(name = "hero_button_text")
    private String heroButtonText;
    
    @Column(name = "hero_primary_button")
    private String heroPrimaryButton;
    
    @Column(name = "hero_secondary_button")
    private String heroSecondaryButton;
    
    @Column(name = "hero_image", columnDefinition = "TEXT")
    private String heroImage;
    
    // Featured Section
    @Column(name = "featured_title")
    private String featuredTitle;
    
    @Column(name = "featured_subtitle")
    private String featuredSubtitle;
    
    // Featured Product 1
    @Column(name = "featured_product_1_image", columnDefinition = "TEXT")
    private String featuredProduct1Image;
    
    @Column(name = "featured_product_1_category")
    private String featuredProduct1Category;
    
    @Column(name = "featured_product_1_name")
    private String featuredProduct1Name;
    
    @Column(name = "featured_product_1_description", columnDefinition = "TEXT")
    private String featuredProduct1Description;
    
    @Column(name = "featured_product_1_price")
    private String featuredProduct1Price;
    
    // Featured Product 2
    @Column(name = "featured_product_2_image", columnDefinition = "TEXT")
    private String featuredProduct2Image;
    
    @Column(name = "featured_product_2_category")
    private String featuredProduct2Category;
    
    @Column(name = "featured_product_2_name")
    private String featuredProduct2Name;
    
    @Column(name = "featured_product_2_description", columnDefinition = "TEXT")
    private String featuredProduct2Description;
    
    @Column(name = "featured_product_2_price")
    private String featuredProduct2Price;
    
    // Featured Product 3
    @Column(name = "featured_product_3_image", columnDefinition = "TEXT")
    private String featuredProduct3Image;
    
    @Column(name = "featured_product_3_category")
    private String featuredProduct3Category;
    
    @Column(name = "featured_product_3_name")
    private String featuredProduct3Name;
    
    @Column(name = "featured_product_3_description", columnDefinition = "TEXT")
    private String featuredProduct3Description;
    
    @Column(name = "featured_product_3_price")
    private String featuredProduct3Price;
    
    // Categories Section
    @Column(name = "categories_title")
    private String categoriesTitle;
    
    @Column(name = "categories_subtitle")
    private String categoriesSubtitle;
    
    // Category Item 1
    @Column(name = "category_item_1_image", columnDefinition = "TEXT")
    private String categoryItem1Image;
    
    @Column(name = "category_item_1_name")
    private String categoryItem1Name;
    
    @Column(name = "category_item_1_count")
    private String categoryItem1Count;
    
    // Category Item 2
    @Column(name = "category_item_2_image", columnDefinition = "TEXT")
    private String categoryItem2Image;
    
    @Column(name = "category_item_2_name")
    private String categoryItem2Name;
    
    @Column(name = "category_item_2_count")
    private String categoryItem2Count;
    
    // Category Item 3
    @Column(name = "category_item_3_image", columnDefinition = "TEXT")
    private String categoryItem3Image;
    
    @Column(name = "category_item_3_name")
    private String categoryItem3Name;
    
    @Column(name = "category_item_3_count")
    private String categoryItem3Count;
    
    // Experience Section
    @Column(name = "experience_title")
    private String experienceTitle;
    
    @Column(name = "experience_subtitle")
    private String experienceSubtitle;
    
    // Gallery Section
    @Column(name = "gallery_title")
    private String galleryTitle;
    
    @Column(name = "gallery_subtitle")
    private String gallerySubtitle;
    
    // Features Section
    @Column(name = "features_title")
    private String featuresTitle;
    
    @Column(name = "features_subtitle")
    private String featuresSubtitle;
    
    // Products Section
    @Column(name = "products_title")
    private String productsTitle;
    
    // Why Choose Section
    @Column(name = "why_choose_title")
    private String whyChooseTitle;
    
    // Artisan Section
    @Column(name = "artisan_title")
    private String artisanTitle;
    
    @Column(name = "artisan_description", columnDefinition = "TEXT")
    private String artisanDescription;
    
    @Column(name = "artisan_link")
    private String artisanLink;
    
    @Column(name = "artisan_image", columnDefinition = "TEXT")
    private String artisanImage;
    
    // Newsletter Section
    @Column(name = "newsletter_title")
    private String newsletterTitle;
    
    @Column(name = "newsletter_text", columnDefinition = "TEXT")
    private String newsletterText;
    
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Dining Info Section
    @Column(name = "dining_info_title")
    private String diningInfoTitle;
    
    @Column(name = "dining_info_description", columnDefinition = "TEXT")
    private String diningInfoDescription;
    
    @Column(name = "dining_info_button")
    private String diningInfoButton;
    
    @Column(name = "dining_info_image", columnDefinition = "TEXT")
    private String diningInfoImage;
}