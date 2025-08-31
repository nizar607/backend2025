package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.Homepage2DTO;
import com.example.stage24.homepage.model.Homepage2;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface Homepage2Service {
    
    /**
     * Get homepage2 by connected user
     */
    Homepage2DTO getHomepage2ByConnectedUser();
    
    /**
     * Get all homepage2 entries
     */
    List<Homepage2DTO> getAllHomepage2s();
    
    /**
     * Get homepage2 by company ID
     */
    Homepage2DTO getHomepage2ByCompanyId(Long companyId);
    
    /**
     * Get homepage2 by website
     */
    Homepage2DTO getHomepage2ByWebsite(String website);
    
    /**
     * Save or update homepage2
     */
    Homepage2DTO saveHomepage2(Homepage2DTO homepage2DTO);
    
    /**
     * Check if homepage2 exists for a company
     */
    boolean existsByCompanyId(Long companyId);
    
    /**
     * Delete homepage2 by company ID
     */
    void deleteHomepage2ByCompanyId(Long companyId);
    
    /**
     * Update homepage2 content
     */
    Homepage2DTO updateHomepage2Content(long id,Homepage2DTO homepage2DTO);
    
    /**
     * Update homepage2 images
     */
    Homepage2DTO updateHomepage2Images(Long id, Map<String, MultipartFile> images);
    
    /**
     * Convert Homepage2 entity to DTO
     */
    Homepage2DTO convertToDTO(Homepage2 homepage2);
    
    /**
     * Convert Homepage2DTO to entity
     */
    Homepage2 convertToEntity(Homepage2DTO homepage2DTO);
}