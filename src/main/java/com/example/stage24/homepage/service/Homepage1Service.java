package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.Homepage1DTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface Homepage1Service {
    
    /**
     * Get homepage1 by connected user's company
     */
    Homepage1DTO getHomepage1ByConnectedUser();
    
    /**
     * Get all homepage1s
     */
    List<Homepage1DTO> getAllHomepage1s();
    
    /**
     * Get homepage1 by company ID
     */
    Optional<Homepage1DTO> getHomepage1ByCompanyId(Long companyId);
    
    /**
     * Save homepage1
     */
    Homepage1DTO saveHomepage1(Homepage1DTO homepage1DTO);
    
    /**
     * Check if homepage1 exists for company
     */
    boolean existsByCompanyId(Long companyId);
    
    /**
     * Delete homepage1 by company ID
     */
    void deleteHomepage1ByCompanyId(Long companyId);
    
    /**
     * Update homepage1 content (text only, preserves images)
     */
    Homepage1DTO updateHomepage1Content(long id, Homepage1DTO homepage1DTO);
    
    /**
     * Update homepage1 images
     */
    Homepage1DTO updateHomepage1Images(
            long id,
            MultipartFile heroBackgroundImage,
            MultipartFile heroImage,
            MultipartFile artisanImage,
            MultipartFile diningInfoImage,
            MultipartFile featuredProduct1Image,
            MultipartFile featuredProduct2Image,
            MultipartFile featuredProduct3Image,
            MultipartFile categoryItem1Image,
            MultipartFile categoryItem2Image,
            MultipartFile categoryItem3Image
    );
    
    /**
     * Convert entity to DTO
     */
    Homepage1DTO convertToDTO(com.example.stage24.homepage.model.Homepage1 homepage1);
    
    /**
     * Convert DTO to entity
     */
    com.example.stage24.homepage.model.Homepage1 convertToEntity(Homepage1DTO dto);
}