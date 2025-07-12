package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "artisan_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homepage_content_id")
    @JsonBackReference
    private HomepageContent homepageContent;
    
    @Column(name = "master_craftspeople_count")
    private Integer masterCraftspeopleCount;
    
    @Column(name = "total_experience_years")
    private Integer totalExperienceYears;
    
    @Column(name = "specialties")
    private String specialties; // Comma-separated list or JSON
    
    @Column(name = "featured_artisan_name")
    private String featuredArtisanName;
    
    @Column(name = "featured_artisan_image")
    private String featuredArtisanImage;
    
    @Column(name = "featured_artisan_bio", columnDefinition = "TEXT")
    private String featuredArtisanBio;
    
    @Column(name = "featured_artisan_specialty")
    private String featuredArtisanSpecialty;
    
    @Column(name = "workshop_location")
    private String workshopLocation;
    
    @Column(name = "quality_guarantee")
    private String qualityGuarantee;
}