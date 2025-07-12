package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "company_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homepage_content_id")
    @JsonBackReference
    private HomepageContent homepageContent;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "number_of_clients")
    private Integer numberOfClients;
    
    @Column(name = "number_of_workers")
    private Integer numberOfWorkers;
    
    @Column(name = "established_year")
    private Integer establishedYear;
    
    @Column(name = "projects_completed")
    private Integer projectsCompleted;
    
    @Column(name = "satisfaction_rate")
    private Double satisfactionRate; // e.g., 98.5 for 98.5%
}