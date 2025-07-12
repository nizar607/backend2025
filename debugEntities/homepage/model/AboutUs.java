package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private com.example.stage24.company.model.Company company;
    
    @Column(name = "cover_image")
    private String coverImage;
    
    // Our Story Section
    @Column(name = "story_title")
    private String storyTitle;
    
    @Column(name = "story_content", columnDefinition = "TEXT")
    private String storyContent;
    
    // Our Values Section
    @Column(name = "values_title")
    private String valuesTitle;
    
    @Column(name = "values_description")
    private String valuesDescription;
    
    @OneToMany(mappedBy = "aboutUs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CompanyValue> companyValues;
    
    // Team Section
    @Column(name = "team_title")
    private String teamTitle;
    
    @Column(name = "team_description")
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
    
    @Column(name = "cta_description")
    private String ctaDescription;
    

}