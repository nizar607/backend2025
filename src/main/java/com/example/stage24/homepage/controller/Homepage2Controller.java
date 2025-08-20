package com.example.stage24.homepage.controller;

import com.example.stage24.homepage.dto.Homepage2DTO;
import com.example.stage24.homepage.service.Homepage2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homepage2")
@RequiredArgsConstructor
@Slf4j
public class Homepage2Controller {
    
    private final Homepage2Service homepage2Service;
    
    @GetMapping
    public ResponseEntity<Homepage2DTO> getHomepage2ByConnectedUser() {
        try {
            Homepage2DTO homepage2 = homepage2Service.getHomepage2ByConnectedUser();
            return ResponseEntity.ok(homepage2);
        } catch (Exception e) {
            log.error("Error retrieving homepage2 for connected user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Homepage2DTO>> getAllHomepage2s() {
        try {
            List<Homepage2DTO> homepage2s = homepage2Service.getAllHomepage2s();
            return ResponseEntity.ok(homepage2s);
        } catch (Exception e) {
            log.error("Error retrieving all homepage2s: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Homepage2DTO> getHomepage2ByCompanyId(@PathVariable Long companyId) {
        try {
            Homepage2DTO homepage2 = homepage2Service.getHomepage2ByCompanyId(companyId);
            return ResponseEntity.ok(homepage2);
        } catch (Exception e) {
            log.error("Error retrieving homepage2 for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Homepage2DTO> createHomepage2(@RequestBody Homepage2DTO homepage2DTO) {
        try {
            Homepage2DTO createdHomepage2 = homepage2Service.saveHomepage2(homepage2DTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHomepage2);
        } catch (Exception e) {
            log.error("Error creating homepage2: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exists/company/{companyId}")
    public ResponseEntity<Boolean> checkHomepage2Exists(@PathVariable Long companyId) {
        try {
            boolean exists = homepage2Service.existsByCompanyId(companyId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking homepage2 existence for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<Void> deleteHomepage2ByCompanyId(@PathVariable Long companyId) {
        try {
            homepage2Service.deleteHomepage2ByCompanyId(companyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting homepage2 for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/content/{id}")
    public ResponseEntity<Homepage2DTO> updateHomepage2Content(
            @PathVariable Long id,
            @RequestBody Homepage2DTO homepage2DTO) {
        try {
            Homepage2DTO updatedHomepage2 = homepage2Service.updateHomepage2Content(id, homepage2DTO);
            return ResponseEntity.ok(updatedHomepage2);
        } catch (Exception e) {
            log.error("Error updating homepage2 content for company {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/images")
    public ResponseEntity<Homepage2DTO> updateHomepage2Images(
            @PathVariable Long id,
            @RequestParam(value = "heroBackgroundImage", required = false) MultipartFile heroBackgroundImage,
            @RequestParam(value = "heroImage", required = false) MultipartFile heroImage,
            @RequestParam(value = "featuredProduct1Image", required = false) MultipartFile featuredProduct1Image,
            @RequestParam(value = "featuredProduct2Image", required = false) MultipartFile featuredProduct2Image,
            @RequestParam(value = "featuredProduct3Image", required = false) MultipartFile featuredProduct3Image,
            @RequestParam(value = "categoryItem1Image", required = false) MultipartFile categoryItem1Image,
            @RequestParam(value = "categoryItem2Image", required = false) MultipartFile categoryItem2Image,
            @RequestParam(value = "categoryItem3Image", required = false) MultipartFile categoryItem3Image,
            @RequestParam(value = "artisanImage", required = false) MultipartFile artisanImage,
            @RequestParam(value = "diningInfoImage", required = false) MultipartFile diningInfoImage,
            @RequestParam(value = "feature1Image", required = false) MultipartFile feature1Image,
            @RequestParam(value = "feature2Image", required = false) MultipartFile feature2Image,
            @RequestParam(value = "feature3Image", required = false) MultipartFile feature3Image,
            @RequestParam(value = "feature4Image", required = false) MultipartFile feature4Image) {
        
        try {
            Map<String, MultipartFile> images = new HashMap<>();
            
            if (heroBackgroundImage != null && !heroBackgroundImage.isEmpty()) {
                images.put("heroBackgroundImage", heroBackgroundImage);
            }
            if (heroImage != null && !heroImage.isEmpty()) {
                images.put("heroImage", heroImage);
            }
            if (featuredProduct1Image != null && !featuredProduct1Image.isEmpty()) {
                images.put("featuredProduct1Image", featuredProduct1Image);
            }
            if (featuredProduct2Image != null && !featuredProduct2Image.isEmpty()) {
                images.put("featuredProduct2Image", featuredProduct2Image);
            }
            if (featuredProduct3Image != null && !featuredProduct3Image.isEmpty()) {
                images.put("featuredProduct3Image", featuredProduct3Image);
            }
            if (categoryItem1Image != null && !categoryItem1Image.isEmpty()) {
                images.put("categoryItem1Image", categoryItem1Image);
            }
            if (categoryItem2Image != null && !categoryItem2Image.isEmpty()) {
                images.put("categoryItem2Image", categoryItem2Image);
            }
            if (categoryItem3Image != null && !categoryItem3Image.isEmpty()) {
                images.put("categoryItem3Image", categoryItem3Image);
            }
            if (artisanImage != null && !artisanImage.isEmpty()) {
                images.put("artisanImage", artisanImage);
            }
            if (diningInfoImage != null && !diningInfoImage.isEmpty()) {
                images.put("diningInfoImage", diningInfoImage);
            }
            if (feature1Image != null && !feature1Image.isEmpty()) {
                images.put("feature1Image", feature1Image);
            }
            if (feature2Image != null && !feature2Image.isEmpty()) {
                images.put("feature2Image", feature2Image);
            }
            if (feature3Image != null && !feature3Image.isEmpty()) {
                images.put("feature3Image", feature3Image);
            }
            if (feature4Image != null && !feature4Image.isEmpty()) {
                images.put("feature4Image", feature4Image);
            }
            
            Homepage2DTO updatedHomepage2 = homepage2Service.updateHomepage2Images(id, images);
            return ResponseEntity.ok(updatedHomepage2);
        } catch (Exception e) {
            log.error("Error updating homepage2 images for company {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}