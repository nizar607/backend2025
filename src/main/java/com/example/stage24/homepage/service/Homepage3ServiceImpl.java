package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.Homepage3DTO;
import com.example.stage24.homepage.model.Homepage3;
import com.example.stage24.homepage.repository.Homepage3Repository;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.company.service.CompanyService;
import com.example.stage24.user.domain.User;
import com.example.stage24.shared.SharedServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class Homepage3ServiceImpl implements Homepage3Service {
    
    private final Homepage3Repository homepage3Repository;
    private final CompanyRepository companyRepository;
    private final SharedServiceInterface sharedService;
    private final FileStorageService fileService;
    private final CompanyService companyService;
    
    @Override
    @Transactional(readOnly = true)
    public Homepage3DTO getHomepage3ByConnectedUser() {
        User user = sharedService.getConnectedUser().orElseThrow(() -> new RuntimeException("User not found"));
        Company company = companyRepository.findByUsersContaining(user)
                .orElseThrow(() -> new RuntimeException("Company by connected user not found"));
        Homepage3 homepage3 = homepage3Repository.findByCompanyId(company.getId())
                .orElseThrow(() -> new RuntimeException("Homepage3 by connected user not found"));
        return convertToDTO(homepage3);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Homepage3DTO> getAllHomepage3s() {
        return homepage3Repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Homepage3DTO getHomepage3ByCompanyId(Long companyId) {
        Homepage3 homepage3 = homepage3Repository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Homepage3 not found for company ID: " + companyId));
        return convertToDTO(homepage3);
    }

    @Override
    @Transactional(readOnly = true)
    public Homepage3DTO getHomepage3ByWebsite(String website) {
        Company company = companyRepository.findByWebsite(website)
                .orElseThrow(() -> new RuntimeException("Company not found for website: " + website));
        Homepage3 homepage3 = homepage3Repository.findByCompanyId(company.getId())
                .orElseThrow(() -> new RuntimeException("Homepage3 not found for website: " + website));
        return convertToDTO(homepage3);
    }
    
    @Override
    public Homepage3DTO saveHomepage3(Homepage3DTO homepage3DTO) {
        Homepage3 homepage3 = convertToEntity(homepage3DTO);
        
        if (homepage3DTO.getCompanyId() != null) {
            Company company = companyRepository.findById(homepage3DTO.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found with ID: " + homepage3DTO.getCompanyId()));
            homepage3.setCompany(company);
        }
        
        Homepage3 savedHomepage3 = homepage3Repository.save(homepage3);
        return convertToDTO(savedHomepage3);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCompanyId(Long companyId) {
        return homepage3Repository.existsByCompanyId(companyId);
    }
    
    @Override
    public void deleteHomepage3ByCompanyId(Long companyId) {
        homepage3Repository.deleteByCompanyId(companyId);
    }
    
    @Override
    public Homepage3DTO updateHomepage3Content(Long id, Homepage3DTO homepage3DTO) {
        Homepage3 existingHomepage3 = homepage3Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homepage3 not found for ID: " + id));
        
        // Update content fields
        updateContentFields(existingHomepage3, homepage3DTO);
        
        Homepage3 updatedHomepage3 = homepage3Repository.save(existingHomepage3);
        
        // Set this homepage as active and others as inactive
        companyService.setHomepageActive(updatedHomepage3.getCompany().getId(), "v3");
        
        return convertToDTO(updatedHomepage3);
    }
    
    @Override
    public Homepage3DTO updateHomepage3Images(Long id, Map<String, MultipartFile> images) {
        Homepage3 existingHomepage3 = homepage3Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homepage3 not found for ID: " + id));
        
        // Update image fields
        updateImageFields(existingHomepage3, images);
        
        Homepage3 updatedHomepage3 = homepage3Repository.save(existingHomepage3);
        return convertToDTO(updatedHomepage3);
    }
    
    @Override
    public Homepage3DTO convertToDTO(Homepage3 homepage3) {
        if (homepage3 == null) {
            return null;
        }
        
        Homepage3DTO dto = new Homepage3DTO();
        dto.setId(homepage3.getId());
        dto.setCompanyId(homepage3.getCompany() != null ? homepage3.getCompany().getId() : null);
        
        // Hero Section
        dto.setHeroTitle(homepage3.getHeroTitle());
        dto.setHeroSubtitle(homepage3.getHeroSubtitle());
        dto.setHeroPrimaryCta(homepage3.getHeroPrimaryCta());
        dto.setHeroSecondaryCta(homepage3.getHeroSecondaryCta());
        dto.setHeroBackgroundImage(homepage3.getHeroBackgroundImage());
        dto.setHeroBadge(homepage3.getHeroBadge());
        dto.setHeroButtonText(homepage3.getHeroButtonText());
        dto.setHeroPrimaryButton(homepage3.getHeroPrimaryButton());
        dto.setHeroSecondaryButton(homepage3.getHeroSecondaryButton());
        dto.setHeroImage(homepage3.getHeroImage());
        
        // Featured Section
        dto.setFeaturedTitle(homepage3.getFeaturedTitle());
        dto.setFeaturedSubtitle(homepage3.getFeaturedSubtitle());
        
        // Featured Products
        dto.setFeaturedProduct1Image(homepage3.getFeaturedProduct1Image());
        dto.setFeaturedProduct1Category(homepage3.getFeaturedProduct1Category());
        dto.setFeaturedProduct1Name(homepage3.getFeaturedProduct1Name());
        dto.setFeaturedProduct1Description(homepage3.getFeaturedProduct1Description());
        dto.setFeaturedProduct1Price(homepage3.getFeaturedProduct1Price());
        
        dto.setFeaturedProduct2Image(homepage3.getFeaturedProduct2Image());
        dto.setFeaturedProduct2Category(homepage3.getFeaturedProduct2Category());
        dto.setFeaturedProduct2Name(homepage3.getFeaturedProduct2Name());
        dto.setFeaturedProduct2Description(homepage3.getFeaturedProduct2Description());
        dto.setFeaturedProduct2Price(homepage3.getFeaturedProduct2Price());
        
        dto.setFeaturedProduct3Image(homepage3.getFeaturedProduct3Image());
        dto.setFeaturedProduct3Category(homepage3.getFeaturedProduct3Category());
        dto.setFeaturedProduct3Name(homepage3.getFeaturedProduct3Name());
        dto.setFeaturedProduct3Description(homepage3.getFeaturedProduct3Description());
        dto.setFeaturedProduct3Price(homepage3.getFeaturedProduct3Price());
        
        // Categories Section
        dto.setCategoriesTitle(homepage3.getCategoriesTitle());
        dto.setCategoriesSubtitle(homepage3.getCategoriesSubtitle());
        
        // Category Items
        dto.setCategoryItem1Image(homepage3.getCategoryItem1Image());
        dto.setCategoryItem1Name(homepage3.getCategoryItem1Name());
        dto.setCategoryItem1Count(homepage3.getCategoryItem1Count());
        
        dto.setCategoryItem2Image(homepage3.getCategoryItem2Image());
        dto.setCategoryItem2Name(homepage3.getCategoryItem2Name());
        dto.setCategoryItem2Count(homepage3.getCategoryItem2Count());
        
        dto.setCategoryItem3Image(homepage3.getCategoryItem3Image());
        dto.setCategoryItem3Name(homepage3.getCategoryItem3Name());
        dto.setCategoryItem3Count(homepage3.getCategoryItem3Count());
        
        // Artisan Section
        dto.setArtisanTitle(homepage3.getArtisanTitle());
        dto.setArtisanDescription(homepage3.getArtisanDescription());
        dto.setArtisanLink(homepage3.getArtisanLink());
        dto.setArtisanImage(homepage3.getArtisanImage());
        
        // Newsletter Section
        dto.setNewsletterTitle(homepage3.getNewsletterTitle());
        dto.setNewsletterText(homepage3.getNewsletterText());
        
        // Dining Info Section
        dto.setDiningInfoTitle(homepage3.getDiningInfoTitle());
        dto.setDiningInfoDescription(homepage3.getDiningInfoDescription());
        dto.setDiningInfoButton(homepage3.getDiningInfoButton());
        dto.setDiningInfoImage(homepage3.getDiningInfoImage());
        
        // Timestamps
        dto.setCreatedAt(homepage3.getCreatedAt());
        dto.setUpdatedAt(homepage3.getUpdatedAt());
        dto.setIsActive(homepage3.getIsActive());
        
        return dto;
    }
    
    @Override
    public Homepage3 convertToEntity(Homepage3DTO dto) {
        if (dto == null) {
            return null;
        }
        
        Homepage3 homepage3 = new Homepage3();
        homepage3.setId(dto.getId());
        
        // Hero Section
        homepage3.setHeroTitle(dto.getHeroTitle());
        homepage3.setHeroSubtitle(dto.getHeroSubtitle());
        homepage3.setHeroPrimaryCta(dto.getHeroPrimaryCta());
        homepage3.setHeroSecondaryCta(dto.getHeroSecondaryCta());
        homepage3.setHeroBackgroundImage(dto.getHeroBackgroundImage());
        homepage3.setHeroBadge(dto.getHeroBadge());
        homepage3.setHeroButtonText(dto.getHeroButtonText());
        homepage3.setHeroPrimaryButton(dto.getHeroPrimaryButton());
        homepage3.setHeroSecondaryButton(dto.getHeroSecondaryButton());
        homepage3.setHeroImage(dto.getHeroImage());
        
        // Featured Section
        homepage3.setFeaturedTitle(dto.getFeaturedTitle());
        homepage3.setFeaturedSubtitle(dto.getFeaturedSubtitle());
        
        // Featured Products
        homepage3.setFeaturedProduct1Image(dto.getFeaturedProduct1Image());
        homepage3.setFeaturedProduct1Category(dto.getFeaturedProduct1Category());
        homepage3.setFeaturedProduct1Name(dto.getFeaturedProduct1Name());
        homepage3.setFeaturedProduct1Description(dto.getFeaturedProduct1Description());
        homepage3.setFeaturedProduct1Price(dto.getFeaturedProduct1Price());
        
        homepage3.setFeaturedProduct2Image(dto.getFeaturedProduct2Image());
        homepage3.setFeaturedProduct2Category(dto.getFeaturedProduct2Category());
        homepage3.setFeaturedProduct2Name(dto.getFeaturedProduct2Name());
        homepage3.setFeaturedProduct2Description(dto.getFeaturedProduct2Description());
        homepage3.setFeaturedProduct2Price(dto.getFeaturedProduct2Price());
        
        homepage3.setFeaturedProduct3Image(dto.getFeaturedProduct3Image());
        homepage3.setFeaturedProduct3Category(dto.getFeaturedProduct3Category());
        homepage3.setFeaturedProduct3Name(dto.getFeaturedProduct3Name());
        homepage3.setFeaturedProduct3Description(dto.getFeaturedProduct3Description());
        homepage3.setFeaturedProduct3Price(dto.getFeaturedProduct3Price());
        
        // Categories Section
        homepage3.setCategoriesTitle(dto.getCategoriesTitle());
        homepage3.setCategoriesSubtitle(dto.getCategoriesSubtitle());
        
        // Category Items
        homepage3.setCategoryItem1Image(dto.getCategoryItem1Image());
        homepage3.setCategoryItem1Name(dto.getCategoryItem1Name());
        homepage3.setCategoryItem1Count(dto.getCategoryItem1Count());
        
        homepage3.setCategoryItem2Image(dto.getCategoryItem2Image());
        homepage3.setCategoryItem2Name(dto.getCategoryItem2Name());
        homepage3.setCategoryItem2Count(dto.getCategoryItem2Count());
        
        homepage3.setCategoryItem3Image(dto.getCategoryItem3Image());
        homepage3.setCategoryItem3Name(dto.getCategoryItem3Name());
        homepage3.setCategoryItem3Count(dto.getCategoryItem3Count());
        
        // Artisan Section
        homepage3.setArtisanTitle(dto.getArtisanTitle());
        homepage3.setArtisanDescription(dto.getArtisanDescription());
        homepage3.setArtisanLink(dto.getArtisanLink());
        homepage3.setArtisanImage(dto.getArtisanImage());
        
        // Newsletter Section
        homepage3.setNewsletterTitle(dto.getNewsletterTitle());
        homepage3.setNewsletterText(dto.getNewsletterText());
        
        // Dining Info Section
        homepage3.setDiningInfoTitle(dto.getDiningInfoTitle());
        homepage3.setDiningInfoDescription(dto.getDiningInfoDescription());
        homepage3.setDiningInfoButton(dto.getDiningInfoButton());
        homepage3.setDiningInfoImage(dto.getDiningInfoImage());
        
        // Timestamps
        homepage3.setCreatedAt(dto.getCreatedAt());
        homepage3.setUpdatedAt(dto.getUpdatedAt());
        homepage3.setIsActive(dto.getIsActive());
        
        return homepage3;
    }
    
    private void updateContentFields(Homepage3 homepage3, Homepage3DTO dto) {
        // Hero Section
        homepage3.setHeroTitle(dto.getHeroTitle());
        homepage3.setHeroSubtitle(dto.getHeroSubtitle());
        homepage3.setHeroPrimaryCta(dto.getHeroPrimaryCta());
        homepage3.setHeroSecondaryCta(dto.getHeroSecondaryCta());
        homepage3.setHeroBadge(dto.getHeroBadge());
        homepage3.setHeroButtonText(dto.getHeroButtonText());
        homepage3.setHeroPrimaryButton(dto.getHeroPrimaryButton());
        homepage3.setHeroSecondaryButton(dto.getHeroSecondaryButton());
        
        // Featured Section
        homepage3.setFeaturedTitle(dto.getFeaturedTitle());
        homepage3.setFeaturedSubtitle(dto.getFeaturedSubtitle());
        
        // Featured Products content (not images)
        homepage3.setFeaturedProduct1Category(dto.getFeaturedProduct1Category());
        homepage3.setFeaturedProduct1Name(dto.getFeaturedProduct1Name());
        homepage3.setFeaturedProduct1Description(dto.getFeaturedProduct1Description());
        homepage3.setFeaturedProduct1Price(dto.getFeaturedProduct1Price());
        
        homepage3.setFeaturedProduct2Category(dto.getFeaturedProduct2Category());
        homepage3.setFeaturedProduct2Name(dto.getFeaturedProduct2Name());
        homepage3.setFeaturedProduct2Description(dto.getFeaturedProduct2Description());
        homepage3.setFeaturedProduct2Price(dto.getFeaturedProduct2Price());
        
        homepage3.setFeaturedProduct3Category(dto.getFeaturedProduct3Category());
        homepage3.setFeaturedProduct3Name(dto.getFeaturedProduct3Name());
        homepage3.setFeaturedProduct3Description(dto.getFeaturedProduct3Description());
        homepage3.setFeaturedProduct3Price(dto.getFeaturedProduct3Price());
        
        // Categories Section
        homepage3.setCategoriesTitle(dto.getCategoriesTitle());
        homepage3.setCategoriesSubtitle(dto.getCategoriesSubtitle());
        
        // Category Items content (not images)
        homepage3.setCategoryItem1Name(dto.getCategoryItem1Name());
        homepage3.setCategoryItem1Count(dto.getCategoryItem1Count());
        homepage3.setCategoryItem2Name(dto.getCategoryItem2Name());
        homepage3.setCategoryItem2Count(dto.getCategoryItem2Count());
        homepage3.setCategoryItem3Name(dto.getCategoryItem3Name());
        homepage3.setCategoryItem3Count(dto.getCategoryItem3Count());
        
        // Artisan Section content (not image)
        homepage3.setArtisanTitle(dto.getArtisanTitle());
        homepage3.setArtisanDescription(dto.getArtisanDescription());
        homepage3.setArtisanLink(dto.getArtisanLink());
        
        // Newsletter Section
        homepage3.setNewsletterTitle(dto.getNewsletterTitle());
        homepage3.setNewsletterText(dto.getNewsletterText());
        
        // Dining Info Section content (not image)
        homepage3.setDiningInfoTitle(dto.getDiningInfoTitle());
        homepage3.setDiningInfoDescription(dto.getDiningInfoDescription());
        homepage3.setDiningInfoButton(dto.getDiningInfoButton());
    }
    
    private void updateImageFields(Homepage3 homepage3, Map<String, MultipartFile> images) {
        images.forEach((fieldName, file) -> {
            if (file != null && !file.isEmpty()) {
                try {
                    String imageUrl = fileService.store(file);
                    
                    switch (fieldName) {
                        case "heroBackgroundImage" -> homepage3.setHeroBackgroundImage(imageUrl);
                        case "heroImage" -> homepage3.setHeroImage(imageUrl);
                        case "featuredProduct1Image" -> homepage3.setFeaturedProduct1Image(imageUrl);
                        case "featuredProduct2Image" -> homepage3.setFeaturedProduct2Image(imageUrl);
                        case "featuredProduct3Image" -> homepage3.setFeaturedProduct3Image(imageUrl);
                        case "categoryItem1Image" -> homepage3.setCategoryItem1Image(imageUrl);
                        case "categoryItem2Image" -> homepage3.setCategoryItem2Image(imageUrl);
                        case "categoryItem3Image" -> homepage3.setCategoryItem3Image(imageUrl);
                        case "artisanImage" -> homepage3.setArtisanImage(imageUrl);
                        case "diningInfoImage" -> homepage3.setDiningInfoImage(imageUrl);
                        default -> log.warn("Unknown image field: {}", fieldName);
                    }
                } catch (Exception e) {
                    log.error("Error saving image for field {}: {}", fieldName, e.getMessage());
                    throw new RuntimeException("Failed to save image for field: " + fieldName, e);
                }
            }
        });
    }
}