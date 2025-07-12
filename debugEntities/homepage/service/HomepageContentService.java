package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.HomepageContentDTO;
import com.example.stage24.homepage.model.*;
import com.example.stage24.homepage.repository.HomepageContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HomepageContentService {
    
    private final HomepageContentRepository homepageContentRepository;
    
    /**
     * Get homepage content by user ID
     */
    @Transactional(readOnly = true)
    public Optional<HomepageContentDTO> getHomepageContentByUserId(Long userId) {
        return homepageContentRepository.findByUserIdWithAllRelations(userId)
                .map(this::convertToDTO);
    }
    
    /**
     * Create or update homepage content
     */
    public HomepageContentDTO saveHomepageContent(HomepageContentDTO dto) {
        HomepageContent entity;
        
        if (dto.getId() != null) {
            entity = homepageContentRepository.findById(dto.getId())
                    .orElse(new HomepageContent());
        } else {
            entity = homepageContentRepository.findByUserId(dto.getUserId())
                    .orElse(new HomepageContent());
        }
        
        updateEntityFromDTO(entity, dto);
        HomepageContent saved = homepageContentRepository.save(entity);
        return convertToDTO(saved);
    }
    
    /**
     * Delete homepage content by user ID
     */
    public void deleteHomepageContentByUserId(Long userId) {
        homepageContentRepository.deleteByUserId(userId);
    }
    
    /**
     * Check if homepage content exists for user
     */
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return homepageContentRepository.existsByUserId(userId);
    }
    
    /**
     * Convert entity to DTO
     */
    private HomepageContentDTO convertToDTO(HomepageContent entity) {
        HomepageContentDTO dto = new HomepageContentDTO();
        
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        
        // Hero section
        dto.setHeroTitle(entity.getHeroTitle());
        dto.setHeroTag(entity.getHeroTag());
        dto.setHeroDescription(entity.getHeroDescription());
        dto.setHeroImage(entity.getHeroImage());
        
        if (entity.getHeroProducts() != null) {
            dto.setHeroProducts(entity.getHeroProducts().stream()
                    .filter(p -> p.getSectionType() == HomepageProduct.SectionType.HERO)
                    .map(this::convertProductToDTO)
                    .collect(Collectors.toList()));
        }
        
        // Section 2
        dto.setSectionTwoTitle(entity.getSectionTwoTitle());
        dto.setSectionTwoDescription(entity.getSectionTwoDescription());
        
        if (entity.getCompanyInfo() != null) {
            dto.setCompanyInfo(convertCompanyInfoToDTO(entity.getCompanyInfo()));
        }
        
        if (entity.getAdvantages() != null) {
            dto.setAdvantages(entity.getAdvantages().stream()
                    .map(this::convertAdvantageToDTO)
                    .collect(Collectors.toList()));
        }
        
        // Section 3
        dto.setSectionThreeTitle(entity.getSectionThreeTitle());
        dto.setSectionThreeDescription(entity.getSectionThreeDescription());
        dto.setSectionThreeCtaText(entity.getSectionThreeCtaText());
        dto.setSectionThreeCtaLink(entity.getSectionThreeCtaLink());
        
        if (entity.getSpotlightProducts() != null) {
            dto.setSpotlightProducts(entity.getSpotlightProducts().stream()
                    .filter(p -> p.getSectionType() == HomepageProduct.SectionType.SPOTLIGHT)
                    .map(this::convertProductToDTO)
                    .collect(Collectors.toList()));
        }
        
        if (entity.getArtisanInfo() != null) {
            dto.setArtisanInfo(convertArtisanInfoToDTO(entity.getArtisanInfo()));
        }
        
        return dto;
    }
    
    /**
     * Update entity from DTO
     */
    private void updateEntityFromDTO(HomepageContent entity, HomepageContentDTO dto) {
        entity.setUserId(dto.getUserId());
        
        // Hero section
        entity.setHeroTitle(dto.getHeroTitle());
        entity.setHeroTag(dto.getHeroTag());
        entity.setHeroDescription(dto.getHeroDescription());
        entity.setHeroImage(dto.getHeroImage());
        
        // Section 2
        entity.setSectionTwoTitle(dto.getSectionTwoTitle());
        entity.setSectionTwoDescription(dto.getSectionTwoDescription());
        
        // Section 3
        entity.setSectionThreeTitle(dto.getSectionThreeTitle());
        entity.setSectionThreeDescription(dto.getSectionThreeDescription());
        entity.setSectionThreeCtaText(dto.getSectionThreeCtaText());
        entity.setSectionThreeCtaLink(dto.getSectionThreeCtaLink());
        
        // Update related entities
        updateProducts(entity, dto);
        updateCompanyInfo(entity, dto.getCompanyInfo());
        updateAdvantages(entity, dto.getAdvantages());
        updateArtisanInfo(entity, dto.getArtisanInfo());
    }
    
    private void updateProducts(HomepageContent entity, HomepageContentDTO dto) {
        // Clear existing products
        if (entity.getHeroProducts() != null) {
            entity.getHeroProducts().clear();
        }
        if (entity.getSpotlightProducts() != null) {
            entity.getSpotlightProducts().clear();
        }
        
        // Add hero products
        if (dto.getHeroProducts() != null) {
            List<HomepageProduct> heroProducts = dto.getHeroProducts().stream()
                    .map(productDTO -> {
                        HomepageProduct product = new HomepageProduct();
                        product.setHomepageContent(entity);
                        product.setProductId(productDTO.getProductId());
                        product.setSectionType(HomepageProduct.SectionType.HERO);
                        product.setDisplayOrder(productDTO.getDisplayOrder());
                        product.setFeatured(productDTO.getFeatured());
                        return product;
                    })
                    .collect(Collectors.toList());
            entity.setHeroProducts(heroProducts);
        }
        
        // Add spotlight products
        if (dto.getSpotlightProducts() != null) {
            List<HomepageProduct> spotlightProducts = dto.getSpotlightProducts().stream()
                    .map(productDTO -> {
                        HomepageProduct product = new HomepageProduct();
                        product.setHomepageContent(entity);
                        product.setProductId(productDTO.getProductId());
                        product.setSectionType(HomepageProduct.SectionType.SPOTLIGHT);
                        product.setDisplayOrder(productDTO.getDisplayOrder());
                        product.setFeatured(productDTO.getFeatured());
                        return product;
                    })
                    .collect(Collectors.toList());
            entity.setSpotlightProducts(spotlightProducts);
        }
    }
    
    private void updateCompanyInfo(HomepageContent entity, HomepageContentDTO.CompanyInfoDTO dto) {
        if (dto != null) {
            CompanyInfo companyInfo = entity.getCompanyInfo();
            if (companyInfo == null) {
                companyInfo = new CompanyInfo();
                companyInfo.setHomepageContent(entity);
                entity.setCompanyInfo(companyInfo);
            }
            
            companyInfo.setYearsOfExperience(dto.getYearsOfExperience());
            companyInfo.setNumberOfClients(dto.getNumberOfClients());
            companyInfo.setNumberOfWorkers(dto.getNumberOfWorkers());
            companyInfo.setEstablishedYear(dto.getEstablishedYear());
            companyInfo.setProjectsCompleted(dto.getProjectsCompleted());
            companyInfo.setSatisfactionRate(dto.getSatisfactionRate());
        }
    }
    
    private void updateAdvantages(HomepageContent entity, List<HomepageContentDTO.CompanyAdvantageDTO> dtos) {
        if (entity.getAdvantages() != null) {
            entity.getAdvantages().clear();
        }
        
        if (dtos != null) {
            List<CompanyAdvantage> advantages = dtos.stream()
                    .map(dto -> {
                        CompanyAdvantage advantage = new CompanyAdvantage();
                        advantage.setHomepageContent(entity);
                        advantage.setAdvantageText(dto.getAdvantageText());
                        advantage.setDisplayOrder(dto.getDisplayOrder());
                        advantage.setIcon(dto.getIcon());
                        advantage.setDescription(dto.getDescription());
                        return advantage;
                    })
                    .collect(Collectors.toList());
            entity.setAdvantages(advantages);
        }
    }
    
    private void updateArtisanInfo(HomepageContent entity, HomepageContentDTO.ArtisanInfoDTO dto) {
        if (dto != null) {
            ArtisanInfo artisanInfo = entity.getArtisanInfo();
            if (artisanInfo == null) {
                artisanInfo = new ArtisanInfo();
                artisanInfo.setHomepageContent(entity);
                entity.setArtisanInfo(artisanInfo);
            }
            
            artisanInfo.setMasterCraftspeopleCount(dto.getMasterCraftspeopleCount());
            artisanInfo.setTotalExperienceYears(dto.getTotalExperienceYears());
            artisanInfo.setSpecialties(dto.getSpecialties());
            artisanInfo.setFeaturedArtisanName(dto.getFeaturedArtisanName());
            artisanInfo.setFeaturedArtisanImage(dto.getFeaturedArtisanImage());
            artisanInfo.setFeaturedArtisanBio(dto.getFeaturedArtisanBio());
            artisanInfo.setFeaturedArtisanSpecialty(dto.getFeaturedArtisanSpecialty());
            artisanInfo.setWorkshopLocation(dto.getWorkshopLocation());
            artisanInfo.setQualityGuarantee(dto.getQualityGuarantee());
        }
    }
    
    // Helper methods for DTO conversion
    private HomepageContentDTO.HomepageProductDTO convertProductToDTO(HomepageProduct product) {
        return new HomepageContentDTO.HomepageProductDTO(
                product.getId(),
                product.getProductId(),
                product.getSectionType().name(),
                product.getDisplayOrder(),
                product.getFeatured()
        );
    }
    
    private HomepageContentDTO.CompanyInfoDTO convertCompanyInfoToDTO(CompanyInfo companyInfo) {
        return new HomepageContentDTO.CompanyInfoDTO(
                companyInfo.getId(),
                companyInfo.getYearsOfExperience(),
                companyInfo.getNumberOfClients(),
                companyInfo.getNumberOfWorkers(),
                companyInfo.getEstablishedYear(),
                companyInfo.getProjectsCompleted(),
                companyInfo.getSatisfactionRate()
        );
    }
    
    private HomepageContentDTO.CompanyAdvantageDTO convertAdvantageToDTO(CompanyAdvantage advantage) {
        return new HomepageContentDTO.CompanyAdvantageDTO(
                advantage.getId(),
                advantage.getAdvantageText(),
                advantage.getDisplayOrder(),
                advantage.getIcon(),
                advantage.getDescription()
        );
    }
    
    private HomepageContentDTO.ArtisanInfoDTO convertArtisanInfoToDTO(ArtisanInfo artisanInfo) {
        return new HomepageContentDTO.ArtisanInfoDTO(
                artisanInfo.getId(),
                artisanInfo.getMasterCraftspeopleCount(),
                artisanInfo.getTotalExperienceYears(),
                artisanInfo.getSpecialties(),
                artisanInfo.getFeaturedArtisanName(),
                artisanInfo.getFeaturedArtisanImage(),
                artisanInfo.getFeaturedArtisanBio(),
                artisanInfo.getFeaturedArtisanSpecialty(),
                artisanInfo.getWorkshopLocation(),
                artisanInfo.getQualityGuarantee()
        );
    }
}