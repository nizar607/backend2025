// package com.example.stage24.homepage.service;

// import com.example.stage24.homepage.dto.HomepageDTO;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// public interface HomepageService {
    
//     /**
//      * Get homepage by connected user's company
//      */
//     HomepageDTO getHomepageByConnectedUser();
    
//     /**
//      * Get all homepages
//      */
//     List<HomepageDTO> getAllHomepages();
    
//     /**
//      * Get homepage by company ID
//      */
//     Optional<HomepageDTO> getHomepageByCompanyId(Long companyId);
    
//     /**
//      * Save homepage
//      */
//     HomepageDTO saveHomepage(HomepageDTO homepageDTO);
    
//     /**
//      * Check if homepage exists for company
//      */
//     boolean existsByCompanyId(Long companyId);
    
//     /**
//      * Delete homepage by company ID
//      */
//     void deleteHomepageByCompanyId(Long companyId);
    
//     /**
//      * Update homepage content (text only, preserves images)
//      */
//     HomepageDTO updateHomepageContent(long id, HomepageDTO homepageDTO);
    
//     /**
//      * Update homepage images
//      */
//     HomepageDTO updateHomepageImages(
//             long id,
//             MultipartFile heroBackgroundImage,
//             MultipartFile heroImage,
//             MultipartFile artisanImage,
//             MultipartFile diningImage,
//             Map<Long, MultipartFile> featuredProductImages,
//             Map<Long, MultipartFile> categoryItemImages,
//             Map<Long, MultipartFile> experienceCardImages,
//             Map<Long, MultipartFile> galleryProductImages,
//             Map<Long, MultipartFile> productItemImages
//     );
    
//     /**
//      * Convert entity to DTO
//      */
//     HomepageDTO convertToDTO(com.example.stage24.homepage.model.Homepage homepage);
    
//     /**
//      * Convert DTO to entity
//      */
//     com.example.stage24.homepage.model.Homepage convertToEntity(HomepageDTO dto);
// }