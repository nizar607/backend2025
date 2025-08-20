package com.example.stage24.homepage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageDTO {
    
    private Long id;
    private Long companyId;
    
    // Hero Section
    private HeroDTO hero;
    
    // Featured Section
    private FeaturedDTO featured;
    
    // Categories Section
    private CategoriesDTO categories;
    
    // Experience Section
    private ExperienceDTO experience;
    
    // Gallery Section
    private GalleryDTO gallery;
    
    // Features Section
    private FeaturesDTO features;
    
    // Products Section
    private ProductsDTO products;
    
    // Stats Section
    private StatsDTO stats;
    
    // Why Choose Section
    private WhyChooseDTO whyChoose;
    
    // Artisan Section
    private ArtisanDTO artisan;
    
    // Newsletter Section
    private NewsletterDTO newsletter;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    
    // Nested DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeroDTO {
        private String title;
        private String subtitle;
        private String primaryCta;
        private String secondaryCta;
        private String backgroundImage;
        private String badge;
        private String buttonText;
        private String primaryButton;
        private String secondaryButton;
        private String image;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeaturedDTO {
        private String title;
        private String subtitle;
        private List<FeaturedProductDTO> products;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriesDTO {
        private String title;
        private String subtitle;
        private List<CategoryItemDTO> items;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceDTO {
        private String title;
        private String subtitle;
        private List<ExperienceCardDTO> cards;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GalleryDTO {
        private String title;
        private String subtitle;
        private List<GalleryProductDTO> products;
        private ArtisanDTO artisan;
        private DiningDTO dining;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeaturesDTO {
        private String title;
        private String subtitle;
        private List<FeatureItemDTO> items;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductsDTO {
        private String title;
        private List<ProductItemDTO> items;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsDTO {
        private List<StatisticDTO> items;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WhyChooseDTO {
        private String title;
        private List<String> features;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtisanDTO {
        private String title;
        private String description;
        private String link;
        private String image;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsletterDTO {
        private String title;
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiningDTO {
        private String title;
        private String description;
        private String button;
        private String image;
    }
    
    // Item DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeaturedProductDTO {
        private Long id;
        private String image;
        private String category;
        private String name;
        private String description;
        private String price;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItemDTO {
        private Long id;
        private String image;
        private String name;
        private String count;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceCardDTO {
        private Long id;
        private String title;
        private String description;
        private String link;
        private String image;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GalleryProductDTO {
        private Long id;
        private String image;
        private String name;
        private String price;
        private String button;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureItemDTO {
        private Long id;
        private String title;
        private String description;
        private String link;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItemDTO {
        private Long id;
        private String image;
        private String badge;
        private String category;
        private String name;
        private String price;
        private String buttonText;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticDTO {
        private Long id;
        private String number;
        private String label;
        private Integer displayOrder;
    }
}