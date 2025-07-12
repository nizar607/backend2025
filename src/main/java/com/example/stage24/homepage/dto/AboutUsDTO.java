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

    private String heroTitle;
    private String heroSubtitle;
    private String coverImage;

    // Our Story Section
    private String storyTitle;
    private String storyContent;
    private String storyText1;
    private String storyText2;
    private String storyText3;
    private String storyImage;

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
        private String title;
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
        private String label;
        private String value;
        private Integer displayOrder;
    }
}