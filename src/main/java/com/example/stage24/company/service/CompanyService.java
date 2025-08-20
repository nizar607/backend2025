package com.example.stage24.company.service;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.model.Company;
import com.example.stage24.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    
    /**
     * Get all companies
     */
    List<CompanyDTO> getAllCompanies();
    
    /**
     * Get all active companies
     */
    List<CompanyDTO> getAllActiveCompanies();
    
    /**
     * Get company by ID
     */
    Optional<CompanyDTO> getCompanyById(Long id);
    
    /**
     * Get company by website
     */
    Optional<CompanyDTO> getCompanyByWebsite(String website);
    
    /**
     * Get company with all related data
     */
    Optional<CompanyDTO> getCompanyWithRelations(Long id);
    
    /**
     * Get company by website with all related data
     */
    Optional<CompanyDTO> getCompanyByWebsiteWithRelations(String website);
    
    /**
     * Create a new company
     */
    CompanyDTO createCompany(CompanyDTO companyDTO,User connectedUser);
    
    /**
     * Update an existing company
     */
    Optional<CompanyDTO> updateCompany(Long id, CompanyDTO companyDTO);
    
    /**
     * Delete a company
     */
    boolean deleteCompany(Long id);
    
    /**
     * Activate/Deactivate a company
     */
    Optional<CompanyDTO> toggleCompanyStatus(Long id);
    
    /**
     * Check if website is available
     */
    boolean isWebsiteAvailable(String website);
    
    /**
     * Check if company name is available
     */
    boolean isCompanyNameAvailable(String name);
    
    /**
     * Convert entity to DTO
     */
    CompanyDTO convertToDTO(Company company);
    
    /**
     * Convert DTO to entity
     */
    Company convertToEntity(CompanyDTO companyDTO);
    
    /**
     * Convert entity to detailed DTO with relations
     */
    CompanyDTO convertToDetailedDTO(Company company);

    /**
     * Update company logo
     */
    CompanyDTO updateCompanyLogo(Long companyId, String logoUrl);
    
    /**
     * Set homepage active status - sets all homepages for a company to inactive except the specified one
     * @param companyId the company ID
     * @param homepageType the homepage type to set as active ("v1", "v2", or "v3")
     */
    void setHomepageActive(Long companyId, String homepageType);
}