package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "homepage_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageStatistic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "number")
    private String number;
    
    @Column(name = "label")
    private String label;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}