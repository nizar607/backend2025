package com.example.stage24.homepage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Homepage2DTO {
    
    private Long id;
    private Long companyId;
    
    // Hero Section
    private String heroTitle;
    private String heroSubtitle;
    private String heroPrimaryCta;
    private String heroSecondaryCta;
    private String heroBackgroundImage;
    private String heroBadge;
    private String heroButtonText;
    private String heroPrimaryButton;
    private String heroSecondaryButton;
    private String heroImage;
    
    // Featured Section
    private String featuredTitle;
    private String featuredSubtitle;
    
    // Featured Product 1
    private String featuredProduct1Image;
    private String featuredProduct1Category;
    private String featuredProduct1Name;
    private String featuredProduct1Description;
    private String featuredProduct1Price;
    
    // Featured Product 2
    private String featuredProduct2Image;
    private String featuredProduct2Category;
    private String featuredProduct2Name;
    private String featuredProduct2Description;
    private String featuredProduct2Price;
    
    // Featured Product 3
    private String featuredProduct3Image;
    private String featuredProduct3Category;
    private String featuredProduct3Name;
    private String featuredProduct3Description;
    private String featuredProduct3Price;
    
    // Categories Section
    private String categoriesTitle;
    private String categoriesSubtitle;
    
    // Category Item 1
    private String categoryItem1Image;
    private String categoryItem1Name;
    private String categoryItem1Count;
    
    // Category Item 2
    private String categoryItem2Image;
    private String categoryItem2Name;
    private String categoryItem2Count;
    
    // Category Item 3
    private String categoryItem3Image;
    private String categoryItem3Name;
    private String categoryItem3Count;
    
    // Experience Section
    private String experienceTitle;
    private String experienceSubtitle;
    
    // Gallery Section
    private String galleryTitle;
    private String gallerySubtitle;
    
    // Features Section
    private String featuresTitle;
    private String featuresSubtitle;
    
    // Feature Items
    private String feature1Number;
    private String feature1Title;
    private String feature1Description;
    private String feature1Link;
    private String feature1Image;
    
    private String feature2Number;
    private String feature2Title;
    private String feature2Description;
    private String feature2Link;
    private String feature2Image;
    
    private String feature3Number;
    private String feature3Title;
    private String feature3Description;
    private String feature3Link;
    private String feature3Image;
    
    private String feature4Number;
    private String feature4Title;
    private String feature4Description;
    private String feature4Link;
    private String feature4Image;
    
    // Products Section
    private String productsTitle;
    
    // Why Choose Section
    private String whyChooseTitle;
    
    // Statistics
    private String statistic1Number;
    private String statistic1Label;
    
    private String statistic2Number;
    private String statistic2Label;
    
    private String statistic3Number;
    private String statistic3Label;
    
    // Why Choose Benefits
    private String benefit1Title;
    private String benefit2Title;
    private String benefit3Title;
    
    // Artisan Section
    private String artisanTitle;
    private String artisanDescription;
    private String artisanLink;
    private String artisanImage;
    
    // Newsletter Section
    private String newsletterTitle;
    private String newsletterText;
    
    // Dining Info Section
    private String diningInfoTitle;
    private String diningInfoDescription;
    private String diningInfoButton;
    private String diningInfoImage;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}