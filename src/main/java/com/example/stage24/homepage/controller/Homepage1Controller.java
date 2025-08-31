package com.example.stage24.homepage.controller;

import com.example.stage24.homepage.dto.Homepage1DTO;
import com.example.stage24.homepage.service.Homepage1Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/homepage1")
@AllArgsConstructor
public class Homepage1Controller {

    private final Homepage1Service homepage1Service;

    @GetMapping
    public ResponseEntity<Homepage1DTO> getHomepage1ByConnectedUser() {
        try {
            Homepage1DTO homepage1 = homepage1Service.getHomepage1ByConnectedUser();
            return ResponseEntity.ok(homepage1);
        } catch (RuntimeException e) {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("all")
    public ResponseEntity<List<Homepage1DTO>> getAllHomepage1s() {
        List<Homepage1DTO> homepage1s = homepage1Service.getAllHomepage1s();
        return ResponseEntity.ok(homepage1s);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Homepage1DTO> getHomepage1ByCompanyId(@PathVariable Long companyId) {
        Optional<Homepage1DTO> homepage1 = homepage1Service.getHomepage1ByCompanyId(companyId);
        return homepage1.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Homepage1DTO> createHomepage1(@RequestBody Homepage1DTO homepage1DTO) {
        try {
            Homepage1DTO savedHomepage1 = homepage1Service.saveHomepage1(homepage1DTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHomepage1);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exists/company/{companyId}")
    public ResponseEntity<Boolean> checkHomepage1Exists(@PathVariable Long companyId) {
        boolean exists = homepage1Service.existsByCompanyId(companyId);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<Void> deleteHomepage1ByCompanyId(@PathVariable Long companyId) {
        try {
            homepage1Service.deleteHomepage1ByCompanyId(companyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update homepage1 content (text-only fields)
     * This endpoint handles all text content updates without affecting images
     */
    @PutMapping("/content/{id}")
    public ResponseEntity<Homepage1DTO> updateHomepage1Content(
            @PathVariable Long id,
            @RequestBody Homepage1DTO homepage1DTO) {
        try {
            Homepage1DTO updatedHomepage1 = homepage1Service.updateHomepage1Content(id, homepage1DTO);
            return ResponseEntity.ok(updatedHomepage1);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update homepage1 images
     * This endpoint handles all image uploads for the homepage1
     */
    @PutMapping("/{id}/images")
    public ResponseEntity<Homepage1DTO> updateHomepage1Images(
            @PathVariable Long id,
            @RequestParam(value = "heroBackgroundImage", required = false) MultipartFile heroBackgroundImage,
            @RequestParam(value = "heroImage", required = false) MultipartFile heroImage,
            @RequestParam(value = "artisanImage", required = false) MultipartFile artisanImage,
            @RequestParam(value = "diningInfoImage", required = false) MultipartFile diningInfoImage,
            @RequestParam(value = "featuredProduct1Image", required = false) MultipartFile featuredProduct1Image,
            @RequestParam(value = "featuredProduct2Image", required = false) MultipartFile featuredProduct2Image,
            @RequestParam(value = "featuredProduct3Image", required = false) MultipartFile featuredProduct3Image,
            @RequestParam(value = "categoryItem1Image", required = false) MultipartFile categoryItem1Image,
            @RequestParam(value = "categoryItem2Image", required = false) MultipartFile categoryItem2Image,
            @RequestParam(value = "categoryItem3Image", required = false) MultipartFile categoryItem3Image) {

        try {
            Homepage1DTO updatedHomepage1 = homepage1Service.updateHomepage1Images(
                    id,
                    heroBackgroundImage,
                    heroImage,
                    artisanImage,
                    diningInfoImage,
                    featuredProduct1Image,
                    featuredProduct2Image,
                    featuredProduct3Image,
                    categoryItem1Image,
                    categoryItem2Image,
                    categoryItem3Image);

            return ResponseEntity.ok(updatedHomepage1);
        } catch (RuntimeException e) {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-website/{website}")
    public ResponseEntity<Homepage1DTO> getHomepage1ByWebsite(@PathVariable String website) {
        Optional<Homepage1DTO> homepage1 = homepage1Service.getHomepage1ByWebsite(website);
        return homepage1.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}