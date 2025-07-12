package com.example.stage24.company.controller;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyController {
    
    private final CompanyService companyService;
    
    /**
     * Get all companies (Super Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get company by client URL
     */
    @GetMapping("/by-url/{clientUrl}")
    public ResponseEntity<CompanyDTO> getCompanyByClientUrl(@PathVariable String clientUrl) {
        return companyService.getCompanyByClientUrl(clientUrl)
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
     * Get company by client URL with all related data
     */
    @GetMapping("/by-url/{clientUrl}/detailed")
    public ResponseEntity<CompanyDTO> getCompanyByClientUrlWithRelations(@PathVariable String clientUrl) {
        return companyService.getCompanyByClientUrlWithRelations(clientUrl)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new company (Super Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        try {
            CompanyDTO createdCompany = companyService.createCompany(companyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update an existing company
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CompanyDTO> toggleCompanyStatus(@PathVariable Long id) {
        return companyService.toggleCompanyStatus(id)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Check if client URL is available
     */
    @GetMapping("/check-url/{clientUrl}")
    public ResponseEntity<Map<String, Boolean>> checkClientUrlAvailability(@PathVariable String clientUrl) {
        boolean available = companyService.isClientUrlAvailable(clientUrl);
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
}