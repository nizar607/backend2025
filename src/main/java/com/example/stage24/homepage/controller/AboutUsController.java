package com.example.stage24.homepage.controller;

import com.example.stage24.homepage.dto.AboutUsDTO;
import com.example.stage24.homepage.service.AboutUsService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/about-us")
@AllArgsConstructor
public class AboutUsController {

    private final AboutUsService aboutUsService;

    @GetMapping
    public ResponseEntity<?> getAboutUsByConnectedUser() {
        try {
            AboutUsDTO aboutUs = aboutUsService.getAboutUsByConnectedUser();
            return ResponseEntity.ok(aboutUs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving About Us content: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<?> getAllAboutUs() {
        try {
            List<AboutUsDTO> aboutUs = aboutUsService.getAllAboutUs();
            return ResponseEntity.ok(aboutUs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving About Us content: " + e.getMessage());
        }
    }

    /**
     * Get about us content by company ID
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAboutUsByCompanyId(@PathVariable Long companyId) {
        try {
            Optional<AboutUsDTO> aboutUs = aboutUsService.getAboutUsByCompanyId(companyId);

            if (aboutUs.isPresent()) {
                return ResponseEntity.ok(aboutUs.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("About Us content not found for company ID: " + companyId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving About Us content: " + e.getMessage());
        }
    }

    /**
     * Create or update about us content
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<?> saveAboutUs(@Valid @RequestBody AboutUsDTO aboutUsDTO) {
        try {
            AboutUsDTO savedAboutUs = aboutUsService.saveAboutUs(aboutUsDTO);
            return ResponseEntity.ok(savedAboutUs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error saving About Us content: " + e.getMessage());
        }
    }

    /**
     * Update existing about us content
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<?> updateAboutUs(@PathVariable Long id, @Valid @RequestBody AboutUsDTO aboutUsDTO) {
        try {
            aboutUsDTO.setId(id);
            AboutUsDTO updatedAboutUs = aboutUsService.saveAboutUs(aboutUsDTO);
            return ResponseEntity.ok(updatedAboutUs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating About Us content: " + e.getMessage());
        }
    }

    /**
     * Delete about us content by company ID
     */
    @DeleteMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAboutUsByCompanyId(@PathVariable Long companyId) {
        try {
            if (aboutUsService.existsByCompanyId(companyId)) {
                aboutUsService.deleteAboutUsByCompanyId(companyId);
                return ResponseEntity.ok("About Us content deleted successfully for company ID: " + companyId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("About Us content not found for company ID: " + companyId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting About Us content: " + e.getMessage());
        }
    }

    /**
     * Check if about us content exists for company
     */
    @GetMapping("/company/{companyId}/exists")
    public ResponseEntity<Boolean> existsByCompanyId(@PathVariable Long companyId) {
        try {
            boolean exists = aboutUsService.existsByCompanyId(companyId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}