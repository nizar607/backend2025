package com.example.stage24.company.service;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.model.Company;

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
     * Get company by client URL
     */
    Optional<CompanyDTO> getCompanyByClientUrl(String clientUrl);
    
    /**
     * Get company with all related data
     */
    Optional<CompanyDTO> getCompanyWithRelations(Long id);
    
    /**
     * Get company by client URL with all related data
     */
    Optional<CompanyDTO> getCompanyByClientUrlWithRelations(String clientUrl);
    
    /**
     * Create a new company
     */
    CompanyDTO createCompany(CompanyDTO companyDTO);
    
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
     * Check if client URL is available
     */
    boolean isClientUrlAvailable(String clientUrl);
    
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
}