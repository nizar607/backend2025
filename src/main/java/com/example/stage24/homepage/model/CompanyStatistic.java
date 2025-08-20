package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "company_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatistic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "about_us_id")
    @JsonBackReference
    private AboutUs aboutUs;
    
    @Column(name = "stat_label")
    private String label;
    
    @Column(name = "stat_value")
    private String value;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}