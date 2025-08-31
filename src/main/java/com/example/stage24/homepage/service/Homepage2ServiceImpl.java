package com.example.stage24.homepage.service;

import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.company.service.CompanyService;
import com.example.stage24.homepage.dto.Homepage2DTO;
import com.example.stage24.homepage.model.Homepage2;
import com.example.stage24.homepage.repository.Homepage2Repository;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class Homepage2ServiceImpl implements Homepage2Service {

    private final Homepage2Repository homepage2Repository;
    private final SharedServiceInterface sharedService;
    private final CompanyRepository companyRepository;
    private final FileStorageService fileService;
    private final CompanyService companyService;
    

    @Override
    public Homepage2DTO getHomepage2ByConnectedUser() {
        User user = sharedService.getConnectedUser().orElseThrow(() -> new RuntimeException("User not found"));
        Company company = companyRepository.findByUsersContaining(user)
                .orElseThrow(() -> new RuntimeException("Company by connected user not found"));
        Homepage2 homepage2 = homepage2Repository.findByCompanyId(company.getId())
                .orElseThrow(() -> new RuntimeException("Homepage2 by connected user not found"));
        return convertToDTO(homepage2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Homepage2DTO> getAllHomepage2s() {
        return homepage2Repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Homepage2DTO getHomepage2ByCompanyId(Long companyId) {
        Homepage2 homepage2 = homepage2Repository.findByCompanyId(companyId)
                .orElseThrow(() -> new RuntimeException("Homepage2 not found for company: " + companyId));
        return convertToDTO(homepage2);
    }

    @Override
    @Transactional(readOnly = true)
    public Homepage2DTO getHomepage2ByWebsite(String website) {
        Company company = companyRepository.findByWebsite(website)
                .orElseThrow(() -> new RuntimeException("Company not found for website: " + website));
        Homepage2 homepage2 = homepage2Repository.findByCompanyId(company.getId())
                .orElseThrow(() -> new RuntimeException("Homepage2 not found for website: " + website));
        return convertToDTO(homepage2);
    }

    @Override
    public Homepage2DTO saveHomepage2(Homepage2DTO homepage2DTO) {
        Homepage2 homepage2 = convertToEntity(homepage2DTO);
        homepage2.setCreatedAt(LocalDateTime.now());
        homepage2.setUpdatedAt(LocalDateTime.now());
        Homepage2 savedHomepage2 = homepage2Repository.save(homepage2);
        return convertToDTO(savedHomepage2);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCompanyId(Long companyId) {
        return homepage2Repository.existsByCompanyId(companyId);
    }

    @Override
    public void deleteHomepage2ByCompanyId(Long companyId) {
        homepage2Repository.deleteByCompanyId(companyId);
    }

    @Override
    public Homepage2DTO updateHomepage2Content(long id,Homepage2DTO homepage2DTO) {
        
        Homepage2 existingHomepage2 = homepage2Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homepage2 not found " + id));
        Company company = companyRepository.findById(existingHomepage2.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found " + existingHomepage2.getCompany().getId()));
        
        // Update content fields
        existingHomepage2.setHeroTitle(homepage2DTO.getHeroTitle());
        existingHomepage2.setHeroSubtitle(homepage2DTO.getHeroSubtitle());
        existingHomepage2.setHeroPrimaryCta(homepage2DTO.getHeroPrimaryCta());
        existingHomepage2.setHeroSecondaryCta(homepage2DTO.getHeroSecondaryCta());
        existingHomepage2.setHeroBadge(homepage2DTO.getHeroBadge());
        existingHomepage2.setHeroButtonText(homepage2DTO.getHeroButtonText());
        existingHomepage2.setHeroPrimaryButton(homepage2DTO.getHeroPrimaryButton());
        existingHomepage2.setHeroSecondaryButton(homepage2DTO.getHeroSecondaryButton());

        existingHomepage2.setFeaturedTitle(homepage2DTO.getFeaturedTitle());
        existingHomepage2.setFeaturedSubtitle(homepage2DTO.getFeaturedSubtitle());
        existingHomepage2.setFeaturedProduct1Category(homepage2DTO.getFeaturedProduct1Category());
        existingHomepage2.setFeaturedProduct1Name(homepage2DTO.getFeaturedProduct1Name());
        existingHomepage2.setFeaturedProduct1Description(homepage2DTO.getFeaturedProduct1Description());
        existingHomepage2.setFeaturedProduct1Price(homepage2DTO.getFeaturedProduct1Price());
        existingHomepage2.setFeaturedProduct2Category(homepage2DTO.getFeaturedProduct2Category());
        existingHomepage2.setFeaturedProduct2Name(homepage2DTO.getFeaturedProduct2Name());
        existingHomepage2.setFeaturedProduct2Description(homepage2DTO.getFeaturedProduct2Description());
        existingHomepage2.setFeaturedProduct2Price(homepage2DTO.getFeaturedProduct2Price());
        existingHomepage2.setFeaturedProduct3Category(homepage2DTO.getFeaturedProduct3Category());
        existingHomepage2.setFeaturedProduct3Name(homepage2DTO.getFeaturedProduct3Name());
        existingHomepage2.setFeaturedProduct3Description(homepage2DTO.getFeaturedProduct3Description());
        existingHomepage2.setFeaturedProduct3Price(homepage2DTO.getFeaturedProduct3Price());

        existingHomepage2.setCategoriesTitle(homepage2DTO.getCategoriesTitle());
        existingHomepage2.setCategoriesSubtitle(homepage2DTO.getCategoriesSubtitle());
        existingHomepage2.setCategoryItem1Name(homepage2DTO.getCategoryItem1Name());
        existingHomepage2.setCategoryItem1Count(homepage2DTO.getCategoryItem1Count());
        existingHomepage2.setCategoryItem2Name(homepage2DTO.getCategoryItem2Name());
        existingHomepage2.setCategoryItem2Count(homepage2DTO.getCategoryItem2Count());
        existingHomepage2.setCategoryItem3Name(homepage2DTO.getCategoryItem3Name());
        existingHomepage2.setCategoryItem3Count(homepage2DTO.getCategoryItem3Count());

        existingHomepage2.setExperienceTitle(homepage2DTO.getExperienceTitle());
        existingHomepage2.setExperienceSubtitle(homepage2DTO.getExperienceSubtitle());
        existingHomepage2.setGalleryTitle(homepage2DTO.getGalleryTitle());
        existingHomepage2.setGallerySubtitle(homepage2DTO.getGallerySubtitle());
        existingHomepage2.setFeaturesTitle(homepage2DTO.getFeaturesTitle());
        existingHomepage2.setFeaturesSubtitle(homepage2DTO.getFeaturesSubtitle());
        
        // Feature Items
        existingHomepage2.setFeature1Number(homepage2DTO.getFeature1Number());
        existingHomepage2.setFeature1Title(homepage2DTO.getFeature1Title());
        existingHomepage2.setFeature1Description(homepage2DTO.getFeature1Description());
        existingHomepage2.setFeature1Link(homepage2DTO.getFeature1Link());
        existingHomepage2.setFeature2Number(homepage2DTO.getFeature2Number());
        existingHomepage2.setFeature2Title(homepage2DTO.getFeature2Title());
        existingHomepage2.setFeature2Description(homepage2DTO.getFeature2Description());
        existingHomepage2.setFeature2Link(homepage2DTO.getFeature2Link());
        existingHomepage2.setFeature3Number(homepage2DTO.getFeature3Number());
        existingHomepage2.setFeature3Title(homepage2DTO.getFeature3Title());
        existingHomepage2.setFeature3Description(homepage2DTO.getFeature3Description());
        existingHomepage2.setFeature3Link(homepage2DTO.getFeature3Link());
        existingHomepage2.setFeature4Number(homepage2DTO.getFeature4Number());
        existingHomepage2.setFeature4Title(homepage2DTO.getFeature4Title());
        existingHomepage2.setFeature4Description(homepage2DTO.getFeature4Description());
        existingHomepage2.setFeature4Link(homepage2DTO.getFeature4Link());
        
        // Why Choose Section
          existingHomepage2.setStatistic1Number(homepage2DTO.getStatistic1Number());
          existingHomepage2.setStatistic1Label(homepage2DTO.getStatistic1Label());
          existingHomepage2.setStatistic2Number(homepage2DTO.getStatistic2Number());
          existingHomepage2.setStatistic2Label(homepage2DTO.getStatistic2Label());
          existingHomepage2.setStatistic3Number(homepage2DTO.getStatistic3Number());
          existingHomepage2.setStatistic3Label(homepage2DTO.getStatistic3Label());
          existingHomepage2.setBenefit1Title(homepage2DTO.getBenefit1Title());
          existingHomepage2.setBenefit2Title(homepage2DTO.getBenefit2Title());
          existingHomepage2.setBenefit3Title(homepage2DTO.getBenefit3Title());
        
        existingHomepage2.setProductsTitle(homepage2DTO.getProductsTitle());
        existingHomepage2.setWhyChooseTitle(homepage2DTO.getWhyChooseTitle());

        existingHomepage2.setArtisanTitle(homepage2DTO.getArtisanTitle());
        existingHomepage2.setArtisanDescription(homepage2DTO.getArtisanDescription());
        existingHomepage2.setArtisanLink(homepage2DTO.getArtisanLink());

        existingHomepage2.setNewsletterTitle(homepage2DTO.getNewsletterTitle());
        existingHomepage2.setNewsletterText(homepage2DTO.getNewsletterText());

        existingHomepage2.setDiningInfoTitle(homepage2DTO.getDiningInfoTitle());
        existingHomepage2.setDiningInfoDescription(homepage2DTO.getDiningInfoDescription());
        existingHomepage2.setDiningInfoButton(homepage2DTO.getDiningInfoButton());

        existingHomepage2.setUpdatedAt(LocalDateTime.now());
        Homepage2 updatedHomepage2 = homepage2Repository.save(existingHomepage2);
        
        // Set this homepage as active and others as inactive
        companyService.setHomepageActive(updatedHomepage2.getCompany().getId(), "v2");
        
        return convertToDTO(updatedHomepage2);
    }

    @Override
    public Homepage2DTO updateHomepage2Images(Long id, Map<String, MultipartFile> images) {
        Homepage2 existingHomepage2 = homepage2Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homepage2 not found: " + id));
        
        // Update image fields
        images.forEach((fieldName, file) -> {
            try {
                String fileName = fileService.store(file);
                switch (fieldName) {
                    case "heroBackgroundImage":
                        existingHomepage2.setHeroBackgroundImage(fileName);
                        break;
                    case "heroImage":
                        existingHomepage2.setHeroImage(fileName);
                        break;
                    case "featuredProduct1Image":
                        existingHomepage2.setFeaturedProduct1Image(fileName);
                        break;
                    case "featuredProduct2Image":
                        existingHomepage2.setFeaturedProduct2Image(fileName);
                        break;
                    case "featuredProduct3Image":
                        existingHomepage2.setFeaturedProduct3Image(fileName);
                        break;
                    case "categoryItem1Image":
                        existingHomepage2.setCategoryItem1Image(fileName);
                        break;
                    case "categoryItem2Image":
                        existingHomepage2.setCategoryItem2Image(fileName);
                        break;
                    case "categoryItem3Image":
                        existingHomepage2.setCategoryItem3Image(fileName);
                        break;
                    case "artisanImage":
                        existingHomepage2.setArtisanImage(fileName);
                        break;
                    case "diningInfoImage":
                        existingHomepage2.setDiningInfoImage(fileName);
                        break;
                    case "feature1Image":
                        existingHomepage2.setFeature1Image(fileName);
                        break;
                    case "feature2Image":
                        existingHomepage2.setFeature2Image(fileName);
                        break;
                    case "feature3Image":
                        existingHomepage2.setFeature3Image(fileName);
                        break;
                    case "feature4Image":
                        existingHomepage2.setFeature4Image(fileName);
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + fieldName, e);
            }
        });

        existingHomepage2.setUpdatedAt(LocalDateTime.now());
        Homepage2 updatedHomepage2 = homepage2Repository.save(existingHomepage2);
        return convertToDTO(updatedHomepage2);
    }

    @Override
    public Homepage2DTO convertToDTO(Homepage2 homepage2) {
        Homepage2DTO dto = new Homepage2DTO();
        dto.setId(homepage2.getId());
        dto.setCompanyId(homepage2.getCompany().getId());
        
        // Hero Section
        dto.setHeroTitle(homepage2.getHeroTitle());
        dto.setHeroSubtitle(homepage2.getHeroSubtitle());
        dto.setHeroPrimaryCta(homepage2.getHeroPrimaryCta());
        dto.setHeroSecondaryCta(homepage2.getHeroSecondaryCta());
        dto.setHeroBackgroundImage(homepage2.getHeroBackgroundImage());
        dto.setHeroBadge(homepage2.getHeroBadge());
        dto.setHeroButtonText(homepage2.getHeroButtonText());
        dto.setHeroPrimaryButton(homepage2.getHeroPrimaryButton());
        dto.setHeroSecondaryButton(homepage2.getHeroSecondaryButton());
        dto.setHeroImage(homepage2.getHeroImage());

        // Featured Section
        dto.setFeaturedTitle(homepage2.getFeaturedTitle());
        dto.setFeaturedSubtitle(homepage2.getFeaturedSubtitle());
        dto.setFeaturedProduct1Image(homepage2.getFeaturedProduct1Image());
        dto.setFeaturedProduct1Category(homepage2.getFeaturedProduct1Category());
        dto.setFeaturedProduct1Name(homepage2.getFeaturedProduct1Name());
        dto.setFeaturedProduct1Description(homepage2.getFeaturedProduct1Description());
        dto.setFeaturedProduct1Price(homepage2.getFeaturedProduct1Price());
        dto.setFeaturedProduct2Image(homepage2.getFeaturedProduct2Image());
        dto.setFeaturedProduct2Category(homepage2.getFeaturedProduct2Category());
        dto.setFeaturedProduct2Name(homepage2.getFeaturedProduct2Name());
        dto.setFeaturedProduct2Description(homepage2.getFeaturedProduct2Description());
        dto.setFeaturedProduct2Price(homepage2.getFeaturedProduct2Price());
        dto.setFeaturedProduct3Image(homepage2.getFeaturedProduct3Image());
        dto.setFeaturedProduct3Category(homepage2.getFeaturedProduct3Category());
        dto.setFeaturedProduct3Name(homepage2.getFeaturedProduct3Name());
        dto.setFeaturedProduct3Description(homepage2.getFeaturedProduct3Description());
        dto.setFeaturedProduct3Price(homepage2.getFeaturedProduct3Price());

        // Categories Section
        dto.setCategoriesTitle(homepage2.getCategoriesTitle());
        dto.setCategoriesSubtitle(homepage2.getCategoriesSubtitle());
        dto.setCategoryItem1Image(homepage2.getCategoryItem1Image());
        dto.setCategoryItem1Name(homepage2.getCategoryItem1Name());
        dto.setCategoryItem1Count(homepage2.getCategoryItem1Count());
        dto.setCategoryItem2Image(homepage2.getCategoryItem2Image());
        dto.setCategoryItem2Name(homepage2.getCategoryItem2Name());
        dto.setCategoryItem2Count(homepage2.getCategoryItem2Count());
        dto.setCategoryItem3Image(homepage2.getCategoryItem3Image());
        dto.setCategoryItem3Name(homepage2.getCategoryItem3Name());
        dto.setCategoryItem3Count(homepage2.getCategoryItem3Count());

        // Experience Section
        dto.setExperienceTitle(homepage2.getExperienceTitle());
        dto.setExperienceSubtitle(homepage2.getExperienceSubtitle());

        // Gallery Section
        dto.setGalleryTitle(homepage2.getGalleryTitle());
        dto.setGallerySubtitle(homepage2.getGallerySubtitle());

        // Features Section
        dto.setFeaturesTitle(homepage2.getFeaturesTitle());
        dto.setFeaturesSubtitle(homepage2.getFeaturesSubtitle());

        // Feature Items
        dto.setFeature1Number(homepage2.getFeature1Number());
        dto.setFeature1Title(homepage2.getFeature1Title());
        dto.setFeature1Description(homepage2.getFeature1Description());
        dto.setFeature1Link(homepage2.getFeature1Link());
        dto.setFeature1Image(homepage2.getFeature1Image());
        dto.setFeature2Number(homepage2.getFeature2Number());
        dto.setFeature2Title(homepage2.getFeature2Title());
        dto.setFeature2Description(homepage2.getFeature2Description());
        dto.setFeature2Link(homepage2.getFeature2Link());
        dto.setFeature2Image(homepage2.getFeature2Image());
        dto.setFeature3Number(homepage2.getFeature3Number());
        dto.setFeature3Title(homepage2.getFeature3Title());
        dto.setFeature3Description(homepage2.getFeature3Description());
        dto.setFeature3Link(homepage2.getFeature3Link());
        dto.setFeature3Image(homepage2.getFeature3Image());
        dto.setFeature4Number(homepage2.getFeature4Number());
        dto.setFeature4Title(homepage2.getFeature4Title());
        dto.setFeature4Description(homepage2.getFeature4Description());
        dto.setFeature4Link(homepage2.getFeature4Link());
        dto.setFeature4Image(homepage2.getFeature4Image());

        // Products Section
        dto.setProductsTitle(homepage2.getProductsTitle());

        // Why Choose Section
        dto.setWhyChooseTitle(homepage2.getWhyChooseTitle());

        // Statistics
        dto.setStatistic1Number(homepage2.getStatistic1Number());
        dto.setStatistic1Label(homepage2.getStatistic1Label());
        dto.setStatistic2Number(homepage2.getStatistic2Number());
        dto.setStatistic2Label(homepage2.getStatistic2Label());
        dto.setStatistic3Number(homepage2.getStatistic3Number());
        dto.setStatistic3Label(homepage2.getStatistic3Label());

        // Why Choose Benefits
        dto.setBenefit1Title(homepage2.getBenefit1Title());
        dto.setBenefit2Title(homepage2.getBenefit2Title());
        dto.setBenefit3Title(homepage2.getBenefit3Title());

        // Artisan Section
        dto.setArtisanTitle(homepage2.getArtisanTitle());
        dto.setArtisanDescription(homepage2.getArtisanDescription());
        dto.setArtisanLink(homepage2.getArtisanLink());
        dto.setArtisanImage(homepage2.getArtisanImage());

        // Newsletter Section
        dto.setNewsletterTitle(homepage2.getNewsletterTitle());
        dto.setNewsletterText(homepage2.getNewsletterText());

        // Dining Info Section
        dto.setDiningInfoTitle(homepage2.getDiningInfoTitle());
        dto.setDiningInfoDescription(homepage2.getDiningInfoDescription());
        dto.setDiningInfoButton(homepage2.getDiningInfoButton());
        dto.setDiningInfoImage(homepage2.getDiningInfoImage());

        dto.setIsActive(homepage2.getIsActive());
        dto.setCreatedAt(homepage2.getCreatedAt());
        dto.setUpdatedAt(homepage2.getUpdatedAt());

        return dto;
    }

    @Override
    public Homepage2 convertToEntity(Homepage2DTO homepage2DTO) {
        Homepage2 homepage2 = new Homepage2();
        
        if (homepage2DTO.getId() != null) {
            homepage2.setId(homepage2DTO.getId());
        }
        
        if (homepage2DTO.getCompanyId() != null) {
            Company company = companyRepository.findById(homepage2DTO.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found: " + homepage2DTO.getCompanyId()));
            homepage2.setCompany(company);
        }

        // Hero Section
        homepage2.setHeroTitle(homepage2DTO.getHeroTitle());
        homepage2.setHeroSubtitle(homepage2DTO.getHeroSubtitle());
        homepage2.setHeroPrimaryCta(homepage2DTO.getHeroPrimaryCta());
        homepage2.setHeroSecondaryCta(homepage2DTO.getHeroSecondaryCta());
        homepage2.setHeroBackgroundImage(homepage2DTO.getHeroBackgroundImage());
        homepage2.setHeroBadge(homepage2DTO.getHeroBadge());
        homepage2.setHeroButtonText(homepage2DTO.getHeroButtonText());
        homepage2.setHeroPrimaryButton(homepage2DTO.getHeroPrimaryButton());
        homepage2.setHeroSecondaryButton(homepage2DTO.getHeroSecondaryButton());
        homepage2.setHeroImage(homepage2DTO.getHeroImage());

        // Featured Section
        homepage2.setFeaturedTitle(homepage2DTO.getFeaturedTitle());
        homepage2.setFeaturedSubtitle(homepage2DTO.getFeaturedSubtitle());
        homepage2.setFeaturedProduct1Image(homepage2DTO.getFeaturedProduct1Image());
        homepage2.setFeaturedProduct1Category(homepage2DTO.getFeaturedProduct1Category());
        homepage2.setFeaturedProduct1Name(homepage2DTO.getFeaturedProduct1Name());
        homepage2.setFeaturedProduct1Description(homepage2DTO.getFeaturedProduct1Description());
        homepage2.setFeaturedProduct1Price(homepage2DTO.getFeaturedProduct1Price());
        homepage2.setFeaturedProduct2Image(homepage2DTO.getFeaturedProduct2Image());
        homepage2.setFeaturedProduct2Category(homepage2DTO.getFeaturedProduct2Category());
        homepage2.setFeaturedProduct2Name(homepage2DTO.getFeaturedProduct2Name());
        homepage2.setFeaturedProduct2Description(homepage2DTO.getFeaturedProduct2Description());
        homepage2.setFeaturedProduct2Price(homepage2DTO.getFeaturedProduct2Price());
        homepage2.setFeaturedProduct3Image(homepage2DTO.getFeaturedProduct3Image());
        homepage2.setFeaturedProduct3Category(homepage2DTO.getFeaturedProduct3Category());
        homepage2.setFeaturedProduct3Name(homepage2DTO.getFeaturedProduct3Name());
        homepage2.setFeaturedProduct3Description(homepage2DTO.getFeaturedProduct3Description());
        homepage2.setFeaturedProduct3Price(homepage2DTO.getFeaturedProduct3Price());

        // Categories Section
        homepage2.setCategoriesTitle(homepage2DTO.getCategoriesTitle());
        homepage2.setCategoriesSubtitle(homepage2DTO.getCategoriesSubtitle());
        homepage2.setCategoryItem1Image(homepage2DTO.getCategoryItem1Image());
        homepage2.setCategoryItem1Name(homepage2DTO.getCategoryItem1Name());
        homepage2.setCategoryItem1Count(homepage2DTO.getCategoryItem1Count());
        homepage2.setCategoryItem2Image(homepage2DTO.getCategoryItem2Image());
        homepage2.setCategoryItem2Name(homepage2DTO.getCategoryItem2Name());
        homepage2.setCategoryItem2Count(homepage2DTO.getCategoryItem2Count());
        homepage2.setCategoryItem3Image(homepage2DTO.getCategoryItem3Image());
        homepage2.setCategoryItem3Name(homepage2DTO.getCategoryItem3Name());
        homepage2.setCategoryItem3Count(homepage2DTO.getCategoryItem3Count());

        // Experience Section
        homepage2.setExperienceTitle(homepage2DTO.getExperienceTitle());
        homepage2.setExperienceSubtitle(homepage2DTO.getExperienceSubtitle());

        // Gallery Section
        homepage2.setGalleryTitle(homepage2DTO.getGalleryTitle());
        homepage2.setGallerySubtitle(homepage2DTO.getGallerySubtitle());

        // Features Section
        homepage2.setFeaturesTitle(homepage2DTO.getFeaturesTitle());
        homepage2.setFeaturesSubtitle(homepage2DTO.getFeaturesSubtitle());

        // Feature Items
        homepage2.setFeature1Number(homepage2DTO.getFeature1Number());
        homepage2.setFeature1Title(homepage2DTO.getFeature1Title());
        homepage2.setFeature1Description(homepage2DTO.getFeature1Description());
        homepage2.setFeature1Link(homepage2DTO.getFeature1Link());
        homepage2.setFeature1Image(homepage2DTO.getFeature1Image());
        homepage2.setFeature2Number(homepage2DTO.getFeature2Number());
        homepage2.setFeature2Title(homepage2DTO.getFeature2Title());
        homepage2.setFeature2Description(homepage2DTO.getFeature2Description());
        homepage2.setFeature2Link(homepage2DTO.getFeature2Link());
        homepage2.setFeature2Image(homepage2DTO.getFeature2Image());
        homepage2.setFeature3Number(homepage2DTO.getFeature3Number());
        homepage2.setFeature3Title(homepage2DTO.getFeature3Title());
        homepage2.setFeature3Description(homepage2DTO.getFeature3Description());
        homepage2.setFeature3Link(homepage2DTO.getFeature3Link());
        homepage2.setFeature3Image(homepage2DTO.getFeature3Image());
        homepage2.setFeature4Number(homepage2DTO.getFeature4Number());
        homepage2.setFeature4Title(homepage2DTO.getFeature4Title());
        homepage2.setFeature4Description(homepage2DTO.getFeature4Description());
        homepage2.setFeature4Link(homepage2DTO.getFeature4Link());
        homepage2.setFeature4Image(homepage2DTO.getFeature4Image());

        // Products Section
        homepage2.setProductsTitle(homepage2DTO.getProductsTitle());

        // Why Choose Section
        homepage2.setWhyChooseTitle(homepage2DTO.getWhyChooseTitle());

        // Statistics
        homepage2.setStatistic1Number(homepage2DTO.getStatistic1Number());
        homepage2.setStatistic1Label(homepage2DTO.getStatistic1Label());
        homepage2.setStatistic2Number(homepage2DTO.getStatistic2Number());
        homepage2.setStatistic2Label(homepage2DTO.getStatistic2Label());
        homepage2.setStatistic3Number(homepage2DTO.getStatistic3Number());
        homepage2.setStatistic3Label(homepage2DTO.getStatistic3Label());

        // Why Choose Benefits
        homepage2.setBenefit1Title(homepage2DTO.getBenefit1Title());
        homepage2.setBenefit2Title(homepage2DTO.getBenefit2Title());
        homepage2.setBenefit3Title(homepage2DTO.getBenefit3Title());

        // Artisan Section
        homepage2.setArtisanTitle(homepage2DTO.getArtisanTitle());
        homepage2.setArtisanDescription(homepage2DTO.getArtisanDescription());
        homepage2.setArtisanLink(homepage2DTO.getArtisanLink());
        homepage2.setArtisanImage(homepage2DTO.getArtisanImage());

        // Newsletter Section
        homepage2.setNewsletterTitle(homepage2DTO.getNewsletterTitle());
        homepage2.setNewsletterText(homepage2DTO.getNewsletterText());

        // Dining Info Section
        homepage2.setDiningInfoTitle(homepage2DTO.getDiningInfoTitle());
        homepage2.setDiningInfoDescription(homepage2DTO.getDiningInfoDescription());
        homepage2.setDiningInfoButton(homepage2DTO.getDiningInfoButton());
        homepage2.setDiningInfoImage(homepage2DTO.getDiningInfoImage());

        homepage2.setIsActive(homepage2DTO.getIsActive());
        homepage2.setCreatedAt(homepage2DTO.getCreatedAt());
        homepage2.setUpdatedAt(homepage2DTO.getUpdatedAt());

        return homepage2;
    }
}