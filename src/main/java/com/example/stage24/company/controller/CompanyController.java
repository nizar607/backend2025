package com.example.stage24.company.controller;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.company.service.CompanyService;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/companies")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final FileStorageService fileService;
    private final CompanyRepository companyRepository;
    private final SharedServiceInterface sharedService;

    /**
     * Get all companies (Super Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Company>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companyRepository.findAll());
    }

    /**
     * Get all active companies
     */
    @GetMapping("/active")
    public ResponseEntity<List<CompanyDTO>> getAllActiveCompanies() {
        List<CompanyDTO> companies = companyService.getAllActiveCompanies();
        return ResponseEntity.ok(companies);
    }

    /**
     * Get company by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get company by website
     */
    @GetMapping("/by-website/{website}")
    public ResponseEntity<CompanyDTO> getCompanyByWebsite(@PathVariable String website) {
        return companyService.getCompanyByWebsite(website)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get company with all related data
     */
    @GetMapping("/{id}/detailed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompanyDTO> getCompanyWithRelations(@PathVariable Long id) {
        return companyService.getCompanyWithRelations(id)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get company by website with all related data
     */
    @GetMapping("/by-url/{website}/detailed")
    public ResponseEntity<CompanyDTO> getCompanyByWebsiteWithRelations(@PathVariable String website) {
        return companyService.getCompanyByWebsiteWithRelations(website)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new company (Super Admin only)
     */
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        try {
            User connectedUser = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("user not found"));
            CompanyDTO createdCompany = companyService.createCompany(companyDTO,connectedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("update-logo/{id}")
    public CompanyDTO updateCompanyLogo(@PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile image) {
        String logoUrl = this.fileService.store(image);
        CompanyDTO updatedCompany = companyService.updateCompanyLogo(Long.parseLong(id), logoUrl);
        return updatedCompany;
    }


    /**
     * Update an existing company
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyDTO companyDTO) {
        try {
            return companyService.updateCompany(id, companyDTO)
                    .map(company -> ResponseEntity.ok(company))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a company (Super Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        if (companyService.deleteCompany(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Toggle company status (activate/deactivate)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> toggleCompanyStatus(@PathVariable Long id) {
        return companyService.toggleCompanyStatus(id)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check if website is available
     */
    @GetMapping("/check-url/{website}")
    public ResponseEntity<Map<String, Boolean>> checkWebsiteAvailability(@PathVariable String website) {
        boolean available = companyService.isWebsiteAvailable(website);
        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * Check if company name is available
     */
    @GetMapping("/check-name/{name}")
    public ResponseEntity<Map<String, Boolean>> checkCompanyNameAvailability(@PathVariable String name) {
        boolean available = companyService.isCompanyNameAvailable(name);
        return ResponseEntity.ok(Map.of("available", available));
    }

    // ===== Version Endpoints =====

    // GET /api/companies/version-by-website?website=foo -> plain text version
    @GetMapping(value = "/version-by-website", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getVersionByWebsite(@RequestParam("website") String website) {

        return companyService.getVersionByWebsite(website)
                .map(version -> ResponseEntity.ok(version != null ? version : ""))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/companies/version/{version} -> update connected company's version using token context
    @PutMapping("/version/{version}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyDTO> updateConnectedCompanyVersion(@PathVariable("version") String version) {
        if (!StringUtils.hasText(version)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(companyService.updateConnectedCompanyVersion(version));
    }

}