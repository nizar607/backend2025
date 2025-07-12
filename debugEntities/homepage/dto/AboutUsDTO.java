package com.example.stage24.homepage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutUsDTO {
    
    private Long id;
    private Long companyId;
    
    private String coverImage;
    
    // Our Story Section
    private String storyTitle;
    private String storyContent;
    
    // Our Values Section
    private String valuesTitle;
    private String valuesDescription;
    private List<CompanyValueDTO> companyValues;
    
    // Team Section
    private String teamTitle;
    private String teamDescription;
    private List<TeamMemberDTO> teamMembers;
    
    // Statistics Section
    private String statsTitle;
    private List<CompanyStatisticDTO> companyStatistics;
    
    // Call to Action Section
    private String ctaTitle;
    private String ctaDescription;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyValueDTO {
        private Long id;
        private String name;
        private String description;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberDTO {
        private Long id;
        private String name;
        private String position;
        private String bio;
        private String image;
        private Integer displayOrder;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyStatisticDTO {
        private Long id;
        private String statLabel;
        private String statValue;
        private Integer displayOrder;
    }
}