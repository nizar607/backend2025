package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.AboutUsDTO;
import com.example.stage24.homepage.model.AboutUs;

import java.util.Optional;

/**
 * Service interface for managing AboutUs entities
 */
public interface AboutUsService {
    
    /**
     * Get about us content by company ID
     * @param companyId the company ID
     * @return Optional containing AboutUsDTO if found
     */
    Optional<AboutUsDTO> getAboutUsByCompanyId(Long companyId);
    
    /**
     * Save or update about us content
     * @param aboutUsDTO the about us data to save
     * @return saved AboutUsDTO
     */
    AboutUsDTO saveAboutUs(AboutUsDTO aboutUsDTO);
    
    /**
     * Check if about us content exists for a company
     * @param companyId the company ID
     * @return true if exists, false otherwise
     */
    boolean existsByCompanyId(Long companyId);
    
    /**
     * Delete about us content by company ID
     * @param companyId the company ID
     */
    void deleteAboutUsByCompanyId(Long companyId);
    
    /**
     * Convert AboutUs entity to DTO
     * @param aboutUs the entity to convert
     * @return converted DTO
     */
    AboutUsDTO convertToDTO(AboutUs aboutUs);
    
    /**
     * Convert AboutUsDTO to entity
     * @param aboutUsDTO the DTO to convert
     * @return converted entity
     */
    AboutUs convertToEntity(AboutUsDTO aboutUsDTO);
}