package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.AboutUsDTO;
import com.example.stage24.homepage.model.AboutUs;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing AboutUs entities
 */
public interface AboutUsService {

    /**
     * Get about us content by company ID
     * 
     * @param companyId the company ID
     * @return Optional containing AboutUsDTO if found
     */

    AboutUsDTO getAboutUsByConnectedUser();

    // getAllAboutUs
    List<AboutUsDTO> getAllAboutUs();

    Optional<AboutUsDTO> getAboutUsByCompanyId(Long companyId);

    /**
     * Save or update about us content
     * 
     * @param aboutUsDTO the about us data to save
     * @return saved AboutUsDTO
     */
    AboutUsDTO saveAboutUs(AboutUsDTO aboutUsDTO);

    /**
     * Check if about us content exists for a company
     * 
     * @param companyId the company ID
     * @return true if exists, false otherwise
     */
    boolean existsByCompanyId(Long companyId);

    /**
     * Delete about us content by company ID
     * 
     * @param companyId the company ID
     */
    void deleteAboutUsByCompanyId(Long companyId);

    /**
     * Convert AboutUs entity to DTO
     * 
     * @param aboutUs the entity to convert
     * @return converted DTO
     */
    AboutUsDTO convertToDTO(AboutUs aboutUs);

    /**
     * Convert AboutUsDTO to entity
     * 
     * @param aboutUsDTO the DTO to convert
     * @return converted entity
     */
    AboutUs convertToEntity(AboutUsDTO aboutUsDTO);

    /**
     * Update AboutUs text content only (without images) based on JWT token
     * authentication
     * Uses the token to get the connected user and verify which company they belong
     * to
     * 
     * @param aboutUsDTO the about us data to update (images will be ignored)
     * @return updated AboutUsDTO
     * @throws RuntimeException if user not found or user has no company or AboutUs
     *                          doesn't exist
     */
    AboutUsDTO updateAboutUsContent(long id, AboutUsDTO aboutUsDTO);

    /**
     * Update AboutUs images using multipart files
     * 
     * @param coverImage       the cover image file (optional)
     * @param storyImage       the story image file (optional)
     * @param teamMemberImages map of team member IDs to their image files
     *                         (optional)
     * @return updated AboutUsDTO
     * @throws RuntimeException if user not found or user has no company or AboutUs
     *                          doesn't exist
     */
    AboutUsDTO updateAboutUsImages(
            long id,
            MultipartFile coverImage,
            MultipartFile storyImage,
            java.util.Map<Long, MultipartFile> teamMemberImages);
}