package com.example.stage24.homepage.service;

import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.company.service.CompanyService;
import com.example.stage24.homepage.dto.Homepage1DTO;
import com.example.stage24.homepage.model.Homepage1;
import com.example.stage24.homepage.repository.Homepage1Repository;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class Homepage1ServiceImpl implements Homepage1Service {

    private final Homepage1Repository homepage1Repository;
    private final SharedServiceInterface sharedService;
    private final CompanyRepository companyRepository;
    private final FileStorageService fileService;
    private final CompanyService companyService;

    @Override
    public Homepage1DTO getHomepage1ByConnectedUser() {
        User user = sharedService.getConnectedUser().orElseThrow(() -> new RuntimeException("User not found"));
        Company company = companyRepository.findByUsersContaining(user)
                .orElseThrow(() -> new RuntimeException("Company by connected user not found"));
        Homepage1 homepage1 = homepage1Repository.findByCompanyId(company.getId())
                .orElseThrow(() -> new RuntimeException("Homepage1 by connected user not found"));
        return convertToDTO(homepage1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Homepage1DTO> getAllHomepage1s() {
        return homepage1Repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Homepage1DTO> getHomepage1ByCompanyId(Long companyId) {
        return homepage1Repository.findByCompanyIdWithCompany(companyId)
                .map(this::convertToDTO);
    }

    @Override
    public Homepage1DTO saveHomepage1(Homepage1DTO homepage1DTO) {
        Homepage1 homepage1 = convertToEntity(homepage1DTO);
        Homepage1 savedHomepage1 = homepage1Repository.save(homepage1);
        return convertToDTO(savedHomepage1);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCompanyId(Long companyId) {
        return homepage1Repository.existsByCompanyId(companyId);
    }

    @Override
    public void deleteHomepage1ByCompanyId(Long companyId) {
        homepage1Repository.deleteByCompanyId(companyId);
    }

    @Override
    public Homepage1DTO updateHomepage1Content(long id, Homepage1DTO homepage1DTO) {
        Homepage1 existingHomepage1 = homepage1Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homepage1 not found"));
        existingHomepage1.setIsActive(true);
        // Preserve existing images
        preserveExistingImages(homepage1DTO, existingHomepage1);

        // Update existing Homepage1
        homepage1DTO.setId(existingHomepage1.getId());
        homepage1DTO.setCompanyId(existingHomepage1.getCompany().getId());

        Homepage1 updatedHomepage1 = convertToEntity(homepage1DTO);
        Homepage1 savedHomepage1 = homepage1Repository.save(updatedHomepage1);
        
        // Set this homepage as active and others as inactive
        companyService.setHomepageActive(savedHomepage1.getCompany().getId(), "v1");
        
        return convertToDTO(savedHomepage1);
    }

    @Override
    public Homepage1DTO updateHomepage1Images(
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
            MultipartFile categoryItem3Image) {

        Homepage1 homepage1 = homepage1Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Homepage1 not found"));

        // Update individual images
        if (heroBackgroundImage != null && !heroBackgroundImage.isEmpty()) {
            String heroBackgroundPath = fileService.store(heroBackgroundImage);
            homepage1.setHeroBackgroundImage(heroBackgroundPath);
        }else{
            System.out.println("null dude null!");
        }

        if (heroImage != null && !heroImage.isEmpty()) {
            String heroImagePath = fileService.store(heroImage);
            homepage1.setHeroImage(heroImagePath);
        }

        if (artisanImage != null && !artisanImage.isEmpty()) {
            String artisanImagePath = fileService.store(artisanImage);
            homepage1.setArtisanImage(artisanImagePath);
        }

        if (diningInfoImage != null && !diningInfoImage.isEmpty()) {
            String diningInfoImagePath = fileService.store(diningInfoImage);
            homepage1.setDiningInfoImage(diningInfoImagePath);
        }

        if (featuredProduct1Image != null && !featuredProduct1Image.isEmpty()) {
            String featuredProduct1ImagePath = fileService.store(featuredProduct1Image);
            homepage1.setFeaturedProduct1Image(featuredProduct1ImagePath);
        }

        if (featuredProduct2Image != null && !featuredProduct2Image.isEmpty()) {
            String featuredProduct2ImagePath = fileService.store(featuredProduct2Image);
            homepage1.setFeaturedProduct2Image(featuredProduct2ImagePath);
        }

        if (featuredProduct3Image != null && !featuredProduct3Image.isEmpty()) {
            String featuredProduct3ImagePath = fileService.store(featuredProduct3Image);
            homepage1.setFeaturedProduct3Image(featuredProduct3ImagePath);
        }

        if (categoryItem1Image != null && !categoryItem1Image.isEmpty()) {
            String categoryItem1ImagePath = fileService.store(categoryItem1Image);
            homepage1.setCategoryItem1Image(categoryItem1ImagePath);
        }

        if (categoryItem2Image != null && !categoryItem2Image.isEmpty()) {
            String categoryItem2ImagePath = fileService.store(categoryItem2Image);
            homepage1.setCategoryItem2Image(categoryItem2ImagePath);
        }

        if (categoryItem3Image != null && !categoryItem3Image.isEmpty()) {
            String categoryItem3ImagePath = fileService.store(categoryItem3Image);
            homepage1.setCategoryItem3Image(categoryItem3ImagePath);
        }

        Homepage1 savedHomepage1 = homepage1Repository.save(homepage1);
        return convertToDTO(savedHomepage1);
    }

    private void preserveExistingImages(Homepage1DTO dto, Homepage1 existing) {
        if (dto.getHeroBackgroundImage() == null) {
            dto.setHeroBackgroundImage(existing.getHeroBackgroundImage());
        }
        if (dto.getHeroImage() == null) {
            dto.setHeroImage(existing.getHeroImage());
        }
        if (dto.getArtisanImage() == null) {
            dto.setArtisanImage(existing.getArtisanImage());
        }
        if (dto.getDiningInfoImage() == null) {
            dto.setDiningInfoImage(existing.getDiningInfoImage());
        }
        if (dto.getFeaturedProduct1Image() == null) {
            dto.setFeaturedProduct1Image(existing.getFeaturedProduct1Image());
        }
        if (dto.getFeaturedProduct2Image() == null) {
            dto.setFeaturedProduct2Image(existing.getFeaturedProduct2Image());
        }
        if (dto.getFeaturedProduct3Image() == null) {
            dto.setFeaturedProduct3Image(existing.getFeaturedProduct3Image());
        }
        if (dto.getCategoryItem1Image() == null) {
            dto.setCategoryItem1Image(existing.getCategoryItem1Image());
        }
        if (dto.getCategoryItem2Image() == null) {
            dto.setCategoryItem2Image(existing.getCategoryItem2Image());
        }
        if (dto.getCategoryItem3Image() == null) {
            dto.setCategoryItem3Image(existing.getCategoryItem3Image());
        }
    }

    @Override
    public Homepage1DTO convertToDTO(Homepage1 homepage1) {
        if (homepage1 == null) {
            return null;
        }

        Homepage1DTO dto = new Homepage1DTO();
        dto.setId(homepage1.getId());
        dto.setCompanyId(homepage1.getCompany() != null ? homepage1.getCompany().getId() : null);
        
        // Hero Section
        dto.setHeroTitle(homepage1.getHeroTitle());
        dto.setHeroSubtitle(homepage1.getHeroSubtitle());
        dto.setHeroPrimaryCta(homepage1.getHeroPrimaryCta());
        dto.setHeroSecondaryCta(homepage1.getHeroSecondaryCta());
        dto.setHeroBackgroundImage(homepage1.getHeroBackgroundImage());
        dto.setHeroBadge(homepage1.getHeroBadge());
        dto.setHeroButtonText(homepage1.getHeroButtonText());
        dto.setHeroPrimaryButton(homepage1.getHeroPrimaryButton());
        dto.setHeroSecondaryButton(homepage1.getHeroSecondaryButton());
        dto.setHeroImage(homepage1.getHeroImage());
        
        // Featured Section
        dto.setFeaturedTitle(homepage1.getFeaturedTitle());
        dto.setFeaturedSubtitle(homepage1.getFeaturedSubtitle());
        
        // Featured Products
        dto.setFeaturedProduct1Image(homepage1.getFeaturedProduct1Image());
        dto.setFeaturedProduct1Category(homepage1.getFeaturedProduct1Category());
        dto.setFeaturedProduct1Name(homepage1.getFeaturedProduct1Name());
        dto.setFeaturedProduct1Description(homepage1.getFeaturedProduct1Description());
        dto.setFeaturedProduct1Price(homepage1.getFeaturedProduct1Price());
        
        dto.setFeaturedProduct2Image(homepage1.getFeaturedProduct2Image());
        dto.setFeaturedProduct2Category(homepage1.getFeaturedProduct2Category());
        dto.setFeaturedProduct2Name(homepage1.getFeaturedProduct2Name());
        dto.setFeaturedProduct2Description(homepage1.getFeaturedProduct2Description());
        dto.setFeaturedProduct2Price(homepage1.getFeaturedProduct2Price());
        
        dto.setFeaturedProduct3Image(homepage1.getFeaturedProduct3Image());
        dto.setFeaturedProduct3Category(homepage1.getFeaturedProduct3Category());
        dto.setFeaturedProduct3Name(homepage1.getFeaturedProduct3Name());
        dto.setFeaturedProduct3Description(homepage1.getFeaturedProduct3Description());
        dto.setFeaturedProduct3Price(homepage1.getFeaturedProduct3Price());
        
        // Categories Section
        dto.setCategoriesTitle(homepage1.getCategoriesTitle());
        dto.setCategoriesSubtitle(homepage1.getCategoriesSubtitle());
        
        // Category Items
        dto.setCategoryItem1Image(homepage1.getCategoryItem1Image());
        dto.setCategoryItem1Name(homepage1.getCategoryItem1Name());
        dto.setCategoryItem1Count(homepage1.getCategoryItem1Count());
        
        dto.setCategoryItem2Image(homepage1.getCategoryItem2Image());
        dto.setCategoryItem2Name(homepage1.getCategoryItem2Name());
        dto.setCategoryItem2Count(homepage1.getCategoryItem2Count());
        
        dto.setCategoryItem3Image(homepage1.getCategoryItem3Image());
        dto.setCategoryItem3Name(homepage1.getCategoryItem3Name());
        dto.setCategoryItem3Count(homepage1.getCategoryItem3Count());
        
        // Artisan Section
        dto.setArtisanTitle(homepage1.getArtisanTitle());
        dto.setArtisanDescription(homepage1.getArtisanDescription());
        dto.setArtisanLink(homepage1.getArtisanLink());
        dto.setArtisanImage(homepage1.getArtisanImage());
        
        // Newsletter Section
        dto.setNewsletterTitle(homepage1.getNewsletterTitle());
        dto.setNewsletterText(homepage1.getNewsletterText());
        
        // Dining Info Section
        dto.setDiningInfoTitle(homepage1.getDiningInfoTitle());
        dto.setDiningInfoDescription(homepage1.getDiningInfoDescription());
        dto.setDiningInfoButton(homepage1.getDiningInfoButton());
        dto.setDiningInfoImage(homepage1.getDiningInfoImage());
        
        // Timestamps
        dto.setCreatedAt(homepage1.getCreatedAt());
        dto.setUpdatedAt(homepage1.getUpdatedAt());
        dto.setIsActive(homepage1.getIsActive());
        
        return dto;
    }

    @Override
    public Homepage1 convertToEntity(Homepage1DTO dto) {
        if (dto == null) {
            return null;
        }

        Homepage1 homepage1 = new Homepage1();
        homepage1.setId(dto.getId());
        
        // Set company relationship
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            homepage1.setCompany(company);
        }
        
        // Hero Section
        homepage1.setHeroTitle(dto.getHeroTitle());
        homepage1.setHeroSubtitle(dto.getHeroSubtitle());
        homepage1.setHeroPrimaryCta(dto.getHeroPrimaryCta());
        homepage1.setHeroSecondaryCta(dto.getHeroSecondaryCta());
        homepage1.setHeroBackgroundImage(dto.getHeroBackgroundImage());
        homepage1.setHeroBadge(dto.getHeroBadge());
        homepage1.setHeroButtonText(dto.getHeroButtonText());
        homepage1.setHeroPrimaryButton(dto.getHeroPrimaryButton());
        homepage1.setHeroSecondaryButton(dto.getHeroSecondaryButton());
        homepage1.setHeroImage(dto.getHeroImage());
        
        // Featured Section
        homepage1.setFeaturedTitle(dto.getFeaturedTitle());
        homepage1.setFeaturedSubtitle(dto.getFeaturedSubtitle());
        
        // Featured Products
        homepage1.setFeaturedProduct1Image(dto.getFeaturedProduct1Image());
        homepage1.setFeaturedProduct1Category(dto.getFeaturedProduct1Category());
        homepage1.setFeaturedProduct1Name(dto.getFeaturedProduct1Name());
        homepage1.setFeaturedProduct1Description(dto.getFeaturedProduct1Description());
        homepage1.setFeaturedProduct1Price(dto.getFeaturedProduct1Price());
        
        homepage1.setFeaturedProduct2Image(dto.getFeaturedProduct2Image());
        homepage1.setFeaturedProduct2Category(dto.getFeaturedProduct2Category());
        homepage1.setFeaturedProduct2Name(dto.getFeaturedProduct2Name());
        homepage1.setFeaturedProduct2Description(dto.getFeaturedProduct2Description());
        homepage1.setFeaturedProduct2Price(dto.getFeaturedProduct2Price());
        
        homepage1.setFeaturedProduct3Image(dto.getFeaturedProduct3Image());
        homepage1.setFeaturedProduct3Category(dto.getFeaturedProduct3Category());
        homepage1.setFeaturedProduct3Name(dto.getFeaturedProduct3Name());
        homepage1.setFeaturedProduct3Description(dto.getFeaturedProduct3Description());
        homepage1.setFeaturedProduct3Price(dto.getFeaturedProduct3Price());
        
        // Categories Section
        homepage1.setCategoriesTitle(dto.getCategoriesTitle());
        homepage1.setCategoriesSubtitle(dto.getCategoriesSubtitle());
        
        // Category Items
        homepage1.setCategoryItem1Image(dto.getCategoryItem1Image());
        homepage1.setCategoryItem1Name(dto.getCategoryItem1Name());
        homepage1.setCategoryItem1Count(dto.getCategoryItem1Count());
        
        homepage1.setCategoryItem2Image(dto.getCategoryItem2Image());
        homepage1.setCategoryItem2Name(dto.getCategoryItem2Name());
        homepage1.setCategoryItem2Count(dto.getCategoryItem2Count());
        
        homepage1.setCategoryItem3Image(dto.getCategoryItem3Image());
        homepage1.setCategoryItem3Name(dto.getCategoryItem3Name());
        homepage1.setCategoryItem3Count(dto.getCategoryItem3Count());
        
        // Artisan Section
        homepage1.setArtisanTitle(dto.getArtisanTitle());
        homepage1.setArtisanDescription(dto.getArtisanDescription());
        homepage1.setArtisanLink(dto.getArtisanLink());
        homepage1.setArtisanImage(dto.getArtisanImage());
        
        // Newsletter Section
        homepage1.setNewsletterTitle(dto.getNewsletterTitle());
        homepage1.setNewsletterText(dto.getNewsletterText());
        
        // Dining Info Section
        homepage1.setDiningInfoTitle(dto.getDiningInfoTitle());
        homepage1.setDiningInfoDescription(dto.getDiningInfoDescription());
        homepage1.setDiningInfoButton(dto.getDiningInfoButton());
        homepage1.setDiningInfoImage(dto.getDiningInfoImage());
        
        // Timestamps
        homepage1.setCreatedAt(dto.getCreatedAt());
        homepage1.setUpdatedAt(dto.getUpdatedAt());
        homepage1.setIsActive(dto.getIsActive());
        
        return homepage1;
    }
}