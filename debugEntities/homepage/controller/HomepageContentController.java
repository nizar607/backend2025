package com.example.stage24.homepage.controller;

import com.example.stage24.homepage.dto.HomepageContentDTO;
import com.example.stage24.homepage.service.HomepageContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/homepage-content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomepageContentController {
    
    private final HomepageContentService homepageContentService;
    
    /**
     * Get homepage content by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<HomepageContentDTO> getHomepageContentByUserId(@PathVariable Long userId) {
        Optional<HomepageContentDTO> content = homepageContentService.getHomepageContentByUserId(userId);
        
        if (content.isPresent()) {
            return ResponseEntity.ok(content.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Create or update homepage content
     */
    @PostMapping
    public ResponseEntity<HomepageContentDTO> createOrUpdateHomepageContent(
            @Valid @RequestBody HomepageContentDTO homepageContentDTO) {
        try {
            HomepageContentDTO savedContent = homepageContentService.saveHomepageContent(homepageContentDTO);
            return ResponseEntity.ok(savedContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update existing homepage content
     */
    @PutMapping("/{id}")
    public ResponseEntity<HomepageContentDTO> updateHomepageContent(
            @PathVariable Long id,
            @Valid @RequestBody HomepageContentDTO homepageContentDTO) {
        try {
            homepageContentDTO.setId(id);
            HomepageContentDTO updatedContent = homepageContentService.saveHomepageContent(homepageContentDTO);
            return ResponseEntity.ok(updatedContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete homepage content by user ID
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteHomepageContentByUserId(@PathVariable Long userId) {
        try {
            if (homepageContentService.existsByUserId(userId)) {
                homepageContentService.deleteHomepageContentByUserId(userId);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Check if homepage content exists for user
     */
    @GetMapping("/user/{userId}/exists")
    public ResponseEntity<Boolean> checkHomepageContentExists(@PathVariable Long userId) {
        boolean exists = homepageContentService.existsByUserId(userId);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Create default homepage content for user
     */
    @PostMapping("/user/{userId}/default")
    public ResponseEntity<HomepageContentDTO> createDefaultHomepageContent(@PathVariable Long userId) {
        try {
            // Check if content already exists
            if (homepageContentService.existsByUserId(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            // Create default content
            HomepageContentDTO defaultContent = createDefaultContent(userId);
            HomepageContentDTO savedContent = homepageContentService.saveHomepageContent(defaultContent);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create default homepage content structure
     */
    private HomepageContentDTO createDefaultContent(Long userId) {
        HomepageContentDTO content = new HomepageContentDTO();
        content.setUserId(userId);
        
        // Default hero section
        content.setHeroTitle("Welcome to Our Furniture Collection");
        content.setHeroTag("Premium Quality");
        content.setHeroDescription("Discover our exquisite collection of handcrafted furniture pieces that combine traditional craftsmanship with modern design.");
        
        // Default section 2
        content.setSectionTwoTitle("Why Choose Us");
        content.setSectionTwoDescription("We are committed to providing the highest quality furniture with exceptional service and craftsmanship.");
        
        // Default company info
        HomepageContentDTO.CompanyInfoDTO companyInfo = new HomepageContentDTO.CompanyInfoDTO();
        companyInfo.setYearsOfExperience(15);
        companyInfo.setNumberOfClients(500);
        companyInfo.setNumberOfWorkers(25);
        companyInfo.setEstablishedYear(2009);
        companyInfo.setProjectsCompleted(1200);
        companyInfo.setSatisfactionRate(98.5);
        content.setCompanyInfo(companyInfo);
        
        // Default section 3
        content.setSectionThreeTitle("Artisan Spotlight");
        content.setSectionThreeDescription("Meet our master craftspeople who bring decades of expertise to every piece. Each item tells a story of passion, precision, and uncompromising quality.");
        content.setSectionThreeCtaText("Meet the Team →");
        content.setSectionThreeCtaLink("/about/team");
        
        // Default artisan info
        HomepageContentDTO.ArtisanInfoDTO artisanInfo = new HomepageContentDTO.ArtisanInfoDTO();
        artisanInfo.setMasterCraftspeopleCount(8);
        artisanInfo.setTotalExperienceYears(120);
        artisanInfo.setSpecialties("Woodworking, Upholstery, Metal Work, Finishing");
        artisanInfo.setQualityGuarantee("Lifetime craftsmanship warranty");
        content.setArtisanInfo(artisanInfo);
        
        return content;
    }
}