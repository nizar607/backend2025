package com.example.stage24.homepage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageContentDTO {
    
    private Long id;
    private Long userId;
    
    // Section 1: Hero Section
    private String heroTitle;
    private String heroTag;
    private String heroDescription;
    private String heroImage;
    private List<HomepageProductDTO> heroProducts;
    
    // Section 2: Company Info Section
    private String sectionTwoTitle;
    private String sectionTwoDescription;
    private CompanyInfoDTO companyInfo;
    private List<CompanyAdvantageDTO> advantages;
    
    // Section 3: Artisan Spotlight Section
    private String sectionThreeTitle;
    private String sectionThreeDescription;
    private String sectionThreeCtaText;
    private String sectionThreeCtaLink;
    private List<HomepageProductDTO> spotlightProducts;
    private ArtisanInfoDTO artisanInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomepageProductDTO {
        private Long id;
        private Long productId;
        private String sectionType;
        private Integer displayOrder;
        private Boolean featured;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfoDTO {
        private Long id;
        private Integer yearsOfExperience;
        private Integer numberOfClients;
        private Integer numberOfWorkers;
        private Integer establishedYear;
        private Integer projectsCompleted;
        private Double satisfactionRate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyAdvantageDTO {
        private Long id;
        private String advantageText;
        private Integer displayOrder;
        private String icon;
        private String description;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtisanInfoDTO {
        private Long id;
        private Integer masterCraftspeopleCount;
        private Integer totalExperienceYears;
        private String specialties;
        private String featuredArtisanName;
        private String featuredArtisanImage;
        private String featuredArtisanBio;
        private String featuredArtisanSpecialty;
        private String workshopLocation;
        private String qualityGuarantee;
    }
}