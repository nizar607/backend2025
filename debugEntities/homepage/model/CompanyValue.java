package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "company_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "about_us_id")
    @JsonBackReference
    private AboutUs aboutUs;
    
    @Column(name = "value_name")
    private String valueName;
    
    @Column(name = "value_description", columnDefinition = "TEXT")
    private String valueDescription;
    
    @Column(name = "value_icon")
    private String valueIcon;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}