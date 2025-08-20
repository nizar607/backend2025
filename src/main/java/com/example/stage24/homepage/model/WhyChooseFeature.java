package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "why_choose_features")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhyChooseFeature {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "feature")
    private String feature;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}