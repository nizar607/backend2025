package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dining_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiningInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "button")
    private String button;
    
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
}