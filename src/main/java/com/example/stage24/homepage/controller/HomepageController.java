// package com.example.stage24.homepage.controller;

// import com.example.stage24.homepage.dto.HomepageDTO;
// import com.example.stage24.homepage.service.HomepageService;
// import lombok.AllArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/homepage")
// @AllArgsConstructor
// public class HomepageController {
    
//     private final HomepageService homepageService;
    
//     @GetMapping
//     public ResponseEntity<HomepageDTO> getHomepageByConnectedUser() {
//         try {
//             HomepageDTO homepage = homepageService.getHomepageByConnectedUser();
//             return ResponseEntity.ok(homepage);
//         } catch (RuntimeException e) {
//             return ResponseEntity.ok().build();
//         }
//     }
    
//     @GetMapping("all")
//     public ResponseEntity<List<HomepageDTO>> getAllHomepages() {
//         List<HomepageDTO> homepages = homepageService.getAllHomepages();
//         return ResponseEntity.ok(homepages);
//     }
    
//     @GetMapping("/company/{companyId}")
//     public ResponseEntity<HomepageDTO> getHomepageByCompanyId(@PathVariable Long companyId) {
//         Optional<HomepageDTO> homepage = homepageService.getHomepageByCompanyId(companyId);
//         return homepage.map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }
    
//     @PostMapping
//     public ResponseEntity<HomepageDTO> createHomepage(@RequestBody HomepageDTO homepageDTO) {
//         try {
//             HomepageDTO savedHomepage = homepageService.saveHomepage(homepageDTO);
//             return ResponseEntity.status(HttpStatus.CREATED).body(savedHomepage);
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().build();
//         }
//     }
    
//     @GetMapping("/exists/company/{companyId}")
//     public ResponseEntity<Boolean> checkHomepageExists(@PathVariable Long companyId) {
//         boolean exists = homepageService.existsByCompanyId(companyId);
//         return ResponseEntity.ok(exists);
//     }
    
//     @DeleteMapping("/company/{companyId}")
//     public ResponseEntity<Void> deleteHomepageByCompanyId(@PathVariable Long companyId) {
//         try {
//             homepageService.deleteHomepageByCompanyId(companyId);
//             return ResponseEntity.noContent().build();
//         } catch (Exception e) {
//             return ResponseEntity.notFound().build();
//         }
//     }
    
//     /**
//      * Update homepage content (text-only fields)
//      * This endpoint handles all text content updates without affecting images
//      */
//     @PutMapping("/content/{id}")
//     public ResponseEntity<HomepageDTO> updateHomepageContent(
//             @PathVariable Long id,
//             @RequestBody HomepageDTO homepageDTO) {
//         try {
//             HomepageDTO updatedHomepage = homepageService.updateHomepageContent(id, homepageDTO);
//             return ResponseEntity.ok(updatedHomepage);
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().build();
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().build();
//         }
//     }
    
//     /**
//      * Update homepage images
//      * This endpoint handles all image uploads for the homepage
//      */
//     @PutMapping("/{id}/images")
//     public ResponseEntity<HomepageDTO> updateHomepageImages(
//             @PathVariable Long id,
//             @RequestParam(value = "heroBackgroundImage", required = false) MultipartFile heroBackgroundImage,
//             @RequestParam(value = "heroImage", required = false) MultipartFile heroImage,
//             @RequestParam(value = "artisanImage", required = false) MultipartFile artisanImage,
//             @RequestParam(value = "diningImage", required = false) MultipartFile diningImage,
//             @RequestParam(value = "featuredProductImages", required = false) Map<String, MultipartFile> featuredProductImagesStr,
//             @RequestParam(value = "categoryItemImages", required = false) Map<String, MultipartFile> categoryItemImagesStr,
//             @RequestParam(value = "experienceCardImages", required = false) Map<String, MultipartFile> experienceCardImagesStr,
//             @RequestParam(value = "galleryProductImages", required = false) Map<String, MultipartFile> galleryProductImagesStr,
//             @RequestParam(value = "productItemImages", required = false) Map<String, MultipartFile> productItemImagesStr) {
        
//         try {
//             // Convert string keys to Long keys for collection images
//             Map<Long, MultipartFile> featuredProductImages = convertStringKeysToLong(featuredProductImagesStr);
//             Map<Long, MultipartFile> categoryItemImages = convertStringKeysToLong(categoryItemImagesStr);
//             Map<Long, MultipartFile> experienceCardImages = convertStringKeysToLong(experienceCardImagesStr);
//             Map<Long, MultipartFile> galleryProductImages = convertStringKeysToLong(galleryProductImagesStr);
//             Map<Long, MultipartFile> productItemImages = convertStringKeysToLong(productItemImagesStr);
            
//             HomepageDTO updatedHomepage = homepageService.updateHomepageImages(
//                     id,
//                     heroBackgroundImage,
//                     heroImage,
//                     artisanImage,
//                     diningImage,
//                     featuredProductImages,
//                     categoryItemImages,
//                     experienceCardImages,
//                     galleryProductImages,
//                     productItemImages
//             );
            
//             return ResponseEntity.ok(updatedHomepage);
//         } catch (RuntimeException e) {
//             return ResponseEntity.ok().build();
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().build();
//         }
//     }
    
//     /**
//      * Helper method to convert string keys to Long keys for image maps
//      */
//     private Map<Long, MultipartFile> convertStringKeysToLong(Map<String, MultipartFile> stringKeyMap) {
//         Map<Long, MultipartFile> longKeyMap = new HashMap<>();
//         if (stringKeyMap != null) {
//             for (Map.Entry<String, MultipartFile> entry : stringKeyMap.entrySet()) {
//                 try {
//                     Long key = Long.parseLong(entry.getKey());
//                     longKeyMap.put(key, entry.getValue());
//                 } catch (NumberFormatException e) {
//                     // Skip invalid keys
//                     continue;
//                 }
//             }
//         }
//         return longKeyMap;
//     }
// }