package com.example.stage24.homepage.controller;

import com.example.stage24.homepage.dto.Homepage3DTO;
import com.example.stage24.homepage.service.Homepage3Service;
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
@RequestMapping("/api/homepage3")
@RequiredArgsConstructor
@Slf4j
public class Homepage3Controller {
    
    private final Homepage3Service homepage3Service;
    
    @GetMapping()
    public ResponseEntity<Homepage3DTO> getHomepage3ByConnectedUser() {
        try {
            Homepage3DTO homepage3 = homepage3Service.getHomepage3ByConnectedUser();
            return ResponseEntity.ok(homepage3);
        } catch (Exception e) {
            log.error("Error retrieving homepage3 for connected user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Homepage3DTO>> getAllHomepage3s() {
        try {
            List<Homepage3DTO> homepage3s = homepage3Service.getAllHomepage3s();
            return ResponseEntity.ok(homepage3s);
        } catch (Exception e) {
            log.error("Error retrieving all homepage3s: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Homepage3DTO> getHomepage3ByCompanyId(@PathVariable Long companyId) {
        try {
            Homepage3DTO homepage3 = homepage3Service.getHomepage3ByCompanyId(companyId);
            return ResponseEntity.ok(homepage3);
        } catch (Exception e) {
            log.error("Error retrieving homepage3 for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Homepage3DTO> createHomepage3(@RequestBody Homepage3DTO homepage3DTO) {
        try {
            Homepage3DTO createdHomepage3 = homepage3Service.saveHomepage3(homepage3DTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHomepage3);
        } catch (Exception e) {
            log.error("Error creating homepage3: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exists/company/{companyId}")
    public ResponseEntity<Boolean> checkHomepage3Exists(@PathVariable Long companyId) {
        try {
            boolean exists = homepage3Service.existsByCompanyId(companyId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking homepage3 existence for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<Void> deleteHomepage3ByCompanyId(@PathVariable Long companyId) {
        try {
            homepage3Service.deleteHomepage3ByCompanyId(companyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting homepage3 for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/content/{id}")
    public ResponseEntity<Homepage3DTO> updateHomepage3Content(
            @PathVariable Long id,
            @RequestBody Homepage3DTO homepage3DTO) {
        try {
            Homepage3DTO updatedHomepage3 = homepage3Service.updateHomepage3Content(id, homepage3DTO);
            return ResponseEntity.ok(updatedHomepage3);
        } catch (Exception e) {
            log.error("Error updating homepage3 content: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/images/{id}")
    public ResponseEntity<Homepage3DTO> updateHomepage3Images(
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
            @RequestParam(value = "diningInfoImage", required = false) MultipartFile diningInfoImage) {
        
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
            
            Homepage3DTO updatedHomepage3 = homepage3Service.updateHomepage3Images(id, images);
            return ResponseEntity.ok(updatedHomepage3);
        } catch (Exception e) {
            log.error("Error updating homepage3 images for id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}