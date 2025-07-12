package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.stage24.company.model.Company;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

@Entity
@Table(name = "about_us")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AboutUs {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    @JsonBackReference
    private Company company;
    
    // heroTitle , heroSubTitle
    @Column(name = "hero_title")
    private String heroTitle;
    
    @Column(name = "hero_subtitle",columnDefinition = "LONGTEXT")
    private String heroSubtitle;

    @Column(name = "cover_image")
    private String coverImage;
    
    // Our Story Section
    @Column(name = "story_title",columnDefinition = "LONGTEXT")
    private String storyTitle;
    
    @Column(name = "story_content", columnDefinition = "LONGTEXT")
    private String storyContent;

    //   storyContent: string; storyText1: string; storyText2: string; storyText3: string; storyImage: string;
    @Column(name = "story_text1",columnDefinition = "LONGTEXT")
    private String storyText1;
    
    @Column(name = "story_text2",columnDefinition = "LONGTEXT")
    private String storyText2;
    
    @Column(name = "story_text3",columnDefinition = "LONGTEXT")
    private String storyText3;
    
    @Column(name = "story_image")
    private String storyImage;
    
    // Our Values Section
    @Column(name = "values_title")
    private String valuesTitle;
    
    @Column(name = "values_description",columnDefinition = "LONGTEXT")
    private String valuesDescription;
    
    @OneToMany(mappedBy = "aboutUs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CompanyValue> companyValues;
    
    // Team Section
    @Column(name = "team_title")
    private String teamTitle;
    
    @Column(name = "team_description",columnDefinition = "LONGTEXT")
    private String teamDescription;
    
    @OneToMany(mappedBy = "aboutUs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TeamMember> teamMembers;
    
    // Statistics Section
    @Column(name = "stats_title")
    private String statsTitle;
    
    @OneToMany(mappedBy = "aboutUs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CompanyStatistic> companyStatistics;
    
    // Call to Action Section
    @Column(name = "cta_title")
    private String ctaTitle;
    
    @Column(name = "cta_description",columnDefinition = "LONGTEXT")
    private String ctaDescription;
    
    @Column(name = "cta_button_text")
    private String ctaButtonText;
    
    @Column(name = "cta_button_link")
    private String ctaButtonLink;

}