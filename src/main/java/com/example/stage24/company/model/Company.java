package com.example.stage24.company.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.example.stage24.homepage.model.AboutUs;
import com.example.stage24.user.domain.User;
// import com.example.stage24.homepage.model.HomepageContent;
// import com.example.stage24.homepage.model.AboutUs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"users", "aboutUs"})
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true, name = "website")
    private String website; // Unique uri
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    private String logo;
        
    @Column(name = "contact_email")
    @Email
    private String email;
    
    @Column(name = "contact_phone")
    private String phoneNumber;
    
    private String address;
    
    @NotNull
    private boolean active = true;
    
    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<User> users = new ArrayList<>();
    
    // @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // @JsonManagedReference
    // private HomepageContent homepageContent;
    
    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private AboutUs aboutUs;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}