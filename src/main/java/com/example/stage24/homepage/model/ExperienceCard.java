package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "experience_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "link")
    private String link;
    
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}