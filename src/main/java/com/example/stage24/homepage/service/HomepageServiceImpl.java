// package com.example.stage24.homepage.service;

// import com.example.stage24.company.model.Company;
// import com.example.stage24.company.repository.CompanyRepository;
// import com.example.stage24.homepage.dto.HomepageDTO;
// import com.example.stage24.homepage.model.*;
// import com.example.stage24.homepage.repository.HomepageRepository;
// import com.example.stage24.shared.FileStorageService;
// import com.example.stage24.shared.SharedServiceInterface;
// import com.example.stage24.user.domain.User;

// import lombok.AllArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// @AllArgsConstructor
// @Transactional
// public class HomepageServiceImpl implements HomepageService {

//     private final HomepageRepository homepageRepository;
//     private final SharedServiceInterface sharedService;
//     private final CompanyRepository companyRepository;
//     private final FileStorageService fileService;

//     @Override
//     public HomepageDTO getHomepageByConnectedUser() {
//         User user = sharedService.getConnectedUser().orElseThrow(() -> new RuntimeException("User not found"));
//         Company company = companyRepository.findByUsersContaining(user)
//                 .orElseThrow(() -> new RuntimeException("Company by connected user not found"));
//         System.out.println(company.getId());
//         Homepage homepage = homepageRepository.findByCompanyId(company.getId())
//                 .orElseThrow(() -> new RuntimeException("Homepage by connected user not found"));
//         System.out.println(homepage.getHeroTitle());
//         return convertToDTO(homepage);
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<HomepageDTO> getAllHomepages() {
//         return homepageRepository.findAll().stream()
//                 .map(this::convertToDTO)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public Optional<HomepageDTO> getHomepageByCompanyId(Long companyId) {
//         return homepageRepository.findByCompanyIdWithAllRelations(companyId)
//                 .map(this::convertToDTO);
//     }

//     @Override
//     public HomepageDTO saveHomepage(HomepageDTO homepageDTO) {
//         Homepage homepage = convertToEntity(homepageDTO);
//         Homepage savedHomepage = homepageRepository.save(homepage);
//         return convertToDTO(savedHomepage);
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public boolean existsByCompanyId(Long companyId) {
//         return homepageRepository.existsByCompanyId(companyId);
//     }

//     @Override
//     public void deleteHomepageByCompanyId(Long companyId) {
//         homepageRepository.deleteByCompanyId(companyId);
//     }

//     @Override
//     public HomepageDTO updateHomepageContent(long id, HomepageDTO homepageDTO) {
//         Homepage existingHomepage = homepageRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Homepage not found"));

//         // Preserve existing images
//         preserveExistingImages(homepageDTO, existingHomepage);

//         // Update existing Homepage
//         homepageDTO.setId(existingHomepage.getId());
//         homepageDTO.setCompanyId(existingHomepage.getCompanyId());

//         Homepage updatedHomepage = convertToEntity(homepageDTO);

//     Long existingDiningId = existingHomepage.getDiningInfo() != null ? existingHomepage.getDiningInfo().getId() : null;
//     if (updatedHomepage.getDiningInfo() != null && existingDiningId != null) {
//         updatedHomepage.getDiningInfo().setId(existingDiningId);
//     }

//     Homepage savedHomepage = homepageRepository.save(updatedHomepage);
//     return convertToDTO(savedHomepage);
//     }

//     @Override
//     public HomepageDTO updateHomepageImages(
//             long id,
//             MultipartFile heroBackgroundImage,
//             MultipartFile heroImage,
//             MultipartFile artisanImage,
//             MultipartFile diningImage,
//             Map<Long, MultipartFile> featuredProductImages,
//             Map<Long, MultipartFile> categoryItemImages,
//             Map<Long, MultipartFile> experienceCardImages,
//             Map<Long, MultipartFile> galleryProductImages,
//             Map<Long, MultipartFile> productItemImages) {

//         Homepage existingHomepage = homepageRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Homepage not found"));

//         // Update hero images
//         if (heroBackgroundImage != null && !heroBackgroundImage.isEmpty()) {
//             String heroBackgroundImagePath = fileService.store(heroBackgroundImage);
//             existingHomepage.setHeroBackgroundImage(heroBackgroundImagePath);
//         }

//         if (heroImage != null && !heroImage.isEmpty()) {
//             String heroImagePath = fileService.store(heroImage);
//             existingHomepage.setHeroImage(heroImagePath);
//         }

//         // Update artisan image
//         if (artisanImage != null && !artisanImage.isEmpty()) {
//             String artisanImagePath = fileService.store(artisanImage);
//             existingHomepage.setArtisanImage(artisanImagePath);
//         }

//         // Update dining image
//         if (diningImage != null && !diningImage.isEmpty() && existingHomepage.getDiningInfo() != null) {
//             String diningImagePath = fileService.store(diningImage);
//             existingHomepage.getDiningInfo().setImage(diningImagePath);
//         }

//         // Update featured product images
//         updateCollectionImages(featuredProductImages, existingHomepage.getFeaturedProducts(),
//                 (product, imagePath) -> product.setImage(imagePath));

//         // Update category item images
//         updateCollectionImages(categoryItemImages, existingHomepage.getCategoryItems(),
//                 (item, imagePath) -> item.setImage(imagePath));

//         // Update experience card images
//         updateCollectionImages(experienceCardImages, existingHomepage.getExperienceCards(),
//                 (card, imagePath) -> card.setImage(imagePath));

//         // Update gallery product images
//         updateCollectionImages(galleryProductImages, existingHomepage.getGalleryProducts(),
//                 (product, imagePath) -> product.setImage(imagePath));

//         // Update product item images
//         updateCollectionImages(productItemImages, existingHomepage.getProductItems(),
//                 (item, imagePath) -> item.setImage(imagePath));

//         Homepage savedHomepage = homepageRepository.save(existingHomepage);
//         return convertToDTO(savedHomepage);
//     }

//     private <T> void updateCollectionImages(Map<Long, MultipartFile> imageMap, List<T> collection,
//             ImageUpdater<T> updater) {
//         if (imageMap != null && !imageMap.isEmpty() && collection != null) {
//             for (T item : collection) {
//                 Long itemId = getItemId(item);
//                 MultipartFile image = imageMap.get(itemId);
//                 if (image != null && !image.isEmpty()) {
//                     String imagePath = fileService.store(image);
//                     updater.updateImage(item, imagePath);
//                 }
//             }
//         }
//     }

//     @FunctionalInterface
//     private interface ImageUpdater<T> {
//         void updateImage(T item, String imagePath);
//     }

//     private <T> Long getItemId(T item) {
//         if (item instanceof FeaturedProduct)
//             return ((FeaturedProduct) item).getId();
//         if (item instanceof CategoryItem)
//             return ((CategoryItem) item).getId();
//         if (item instanceof ExperienceCard)
//             return ((ExperienceCard) item).getId();
//         if (item instanceof GalleryProduct)
//             return ((GalleryProduct) item).getId();
//         if (item instanceof ProductItem)
//             return ((ProductItem) item).getId();
//         return null;
//     }

//     private void preserveExistingImages(HomepageDTO dto, Homepage existing) {
//         // Preserve hero images
//         if (dto.getHero() != null) {
//             dto.getHero().setBackgroundImage(existing.getHeroBackgroundImage());
//             dto.getHero().setImage(existing.getHeroImage());
//         }

//         // Preserve artisan image
//         if (dto.getArtisan() != null) {
//             dto.getArtisan().setImage(existing.getArtisanImage());
//         }

//         // Preserve dining image
//         if (dto.getGallery() != null && dto.getGallery().getDining() != null && existing.getDiningInfo() != null) {
//             dto.getGallery().getDining().setImage(existing.getDiningInfo().getImage());
//         }

//         // Preserve collection images
//         preserveCollectionImages(dto.getFeatured(), existing.getFeaturedProducts());
//         preserveCollectionImages(dto.getCategories(), existing.getCategoryItems());
//         preserveCollectionImages(dto.getExperience(), existing.getExperienceCards());
//         preserveCollectionImages(dto.getGallery(), existing.getGalleryProducts());
//         preserveCollectionImages(dto.getProducts(), existing.getProductItems());
//     }

//     private <T, U> void preserveCollectionImages(T dtoSection, List<U> existingItems) {
//         // Implementation would depend on specific section type
//         // This is a simplified version - full implementation would handle each section
//         // type
//     }

//     @Override
//     public HomepageDTO convertToDTO(Homepage homepage) {
//         if (homepage == null) {
//             return null;
//         }

//         HomepageDTO dto = new HomepageDTO();
//         dto.setId(homepage.getId());
//         dto.setCompanyId(homepage.getCompanyId());
//         dto.setCreatedAt(homepage.getCreatedAt());
//         dto.setUpdatedAt(homepage.getUpdatedAt());
//         dto.setIsActive(homepage.getIsActive());

//         // Convert hero section
//         dto.setHero(convertHeroToDTO(homepage));

//         // Convert featured section
//         dto.setFeatured(convertFeaturedToDTO(homepage));

//         // Convert categories section
//         dto.setCategories(convertCategoriesToDTO(homepage));

//         // Convert experience section
//         dto.setExperience(convertExperienceToDTO(homepage));

//         // Convert gallery section
//         dto.setGallery(convertGalleryToDTO(homepage));

//         // Convert features section
//         dto.setFeatures(convertFeaturesToDTO(homepage));

//         // Convert products section
//         dto.setProducts(convertProductsToDTO(homepage));

//         // Convert stats section
//         dto.setStats(convertStatsToDTO(homepage));

//         // Convert why choose section
//         dto.setWhyChoose(convertWhyChooseToDTO(homepage));

//         // Convert artisan section
//         dto.setArtisan(convertArtisanToDTO(homepage));

//         // Convert newsletter section
//         dto.setNewsletter(convertNewsletterToDTO(homepage));

//         return dto;
//     }

//     @Override
//     public Homepage convertToEntity(HomepageDTO dto) {
//         if (dto == null) {
//             return null;
//         }

//         Homepage homepage = new Homepage();
//         homepage.setId(dto.getId());
//         homepage.setCompanyId(dto.getCompanyId());
//         homepage.setIsActive(dto.getIsActive());

//         // Convert hero section
//         convertHeroToEntity(dto.getHero(), homepage);

//         // Convert featured section
//         convertFeaturedToEntity(dto.getFeatured(), homepage);

//         // Convert categories section
//         convertCategoriesToEntity(dto.getCategories(), homepage);

//         // Convert experience section
//         convertExperienceToEntity(dto.getExperience(), homepage);

//         // Convert gallery section
//         convertGalleryToEntity(dto.getGallery(), homepage);

//         // Convert features section
//         convertFeaturesToEntity(dto.getFeatures(), homepage);

//         // Convert products section
//         convertProductsToEntity(dto.getProducts(), homepage);

//         // Convert stats section
//         convertStatsToEntity(dto.getStats(), homepage);

//         // Convert why choose section
//         convertWhyChooseToEntity(dto.getWhyChoose(), homepage);

//         // Convert artisan section
//         convertArtisanToEntity(dto.getArtisan(), homepage);

//         // Convert newsletter section
//         convertNewsletterToEntity(dto.getNewsletter(), homepage);

//         return homepage;
//     }

//     // Helper methods for DTO conversion
//     private HomepageDTO.HeroDTO convertHeroToDTO(Homepage homepage) {
//         HomepageDTO.HeroDTO hero = new HomepageDTO.HeroDTO();
//         hero.setTitle(homepage.getHeroTitle());
//         hero.setSubtitle(homepage.getHeroSubtitle());
//         hero.setPrimaryCta(homepage.getHeroPrimaryCta());
//         hero.setSecondaryCta(homepage.getHeroSecondaryCta());
//         hero.setBackgroundImage(homepage.getHeroBackgroundImage());
//         hero.setBadge(homepage.getHeroBadge());
//         hero.setButtonText(homepage.getHeroButtonText());
//         hero.setPrimaryButton(homepage.getHeroPrimaryButton());
//         hero.setSecondaryButton(homepage.getHeroSecondaryButton());
//         hero.setImage(homepage.getHeroImage());
//         return hero;
//     }

//     private HomepageDTO.FeaturedDTO convertFeaturedToDTO(Homepage homepage) {
//         HomepageDTO.FeaturedDTO featured = new HomepageDTO.FeaturedDTO();
//         featured.setTitle(homepage.getFeaturedTitle());
//         featured.setSubtitle(homepage.getFeaturedSubtitle());
//         if (homepage.getFeaturedProducts() != null) {
//             featured.setProducts(homepage.getFeaturedProducts().stream()
//                     .map(this::convertFeaturedProductToDTO)
//                     .collect(Collectors.toList()));
//         }
//         return featured;
//     }

//     private HomepageDTO.CategoriesDTO convertCategoriesToDTO(Homepage homepage) {
//         HomepageDTO.CategoriesDTO categories = new HomepageDTO.CategoriesDTO();
//         categories.setTitle(homepage.getCategoriesTitle());
//         categories.setSubtitle(homepage.getCategoriesSubtitle());
//         if (homepage.getCategoryItems() != null) {
//             categories.setItems(homepage.getCategoryItems().stream()
//                     .map(this::convertCategoryItemToDTO)
//                     .collect(Collectors.toList()));
//         }
//         return categories;
//     }

//     private HomepageDTO.ExperienceDTO convertExperienceToDTO(Homepage homepage) {
//         HomepageDTO.ExperienceDTO experience = new HomepageDTO.ExperienceDTO();
//         experience.setTitle(homepage.getExperienceTitle());
//         experience.setSubtitle(homepage.getExperienceSubtitle());
//         if (homepage.getExperienceCards() != null) {
//             experience.setCards(homepage.getExperienceCards().stream()
//                     .map(this::convertExperienceCardToDTO)
//                     .collect(Collectors.toList()));
//         }
//         return experience;
//     }

//     private HomepageDTO.GalleryDTO convertGalleryToDTO(Homepage homepage) {
//         HomepageDTO.GalleryDTO gallery = new HomepageDTO.GalleryDTO();
//         gallery.setTitle(homepage.getGalleryTitle());
//         gallery.setSubtitle(homepage.getGallerySubtitle());
//         if (homepage.getGalleryProducts() != null) {
//             gallery.setProducts(homepage.getGalleryProducts().stream()
//                     .map(this::convertGalleryProductToDTO)
//                     .collect(Collectors.toList()));
//         }
//         gallery.setArtisan(convertArtisanToDTO(homepage));
//         if (homepage.getDiningInfo() != null) {
//             gallery.setDining(convertDiningToDTO(homepage.getDiningInfo()));
//         }
//         return gallery;
//     }

//     private HomepageDTO.FeaturesDTO convertFeaturesToDTO(Homepage homepage) {
//         HomepageDTO.FeaturesDTO features = new HomepageDTO.FeaturesDTO();
//         features.setTitle(homepage.getFeaturesTitle());
//         features.setSubtitle(homepage.getFeaturesSubtitle());
//         if (homepage.getFeatureItems() != null) {
//             features.setItems(homepage.getFeatureItems().stream()
//                     .map(this::convertFeatureItemToDTO)
//                     .collect(Collectors.toList()));
//         }
//         return features;
//     }

//     private HomepageDTO.ProductsDTO convertProductsToDTO(Homepage homepage) {
//         HomepageDTO.ProductsDTO products = new HomepageDTO.ProductsDTO();
//         products.setTitle(homepage.getProductsTitle());
//         if (homepage.getProductItems() != null) {
//             products.setItems(homepage.getProductItems().stream()
//                     .map(this::convertProductItemToDTO)
//                     .collect(Collectors.toList()));
//         }
//         return products;
//     }

//     private HomepageDTO.StatsDTO convertStatsToDTO(Homepage homepage) {
//         HomepageDTO.StatsDTO stats = new HomepageDTO.StatsDTO();
//         if (homepage.getStatistics() != null) {
//             stats.setItems(homepage.getStatistics().stream()
//                     .map(this::convertStatisticToDTO)
//                     .collect(Collectors.toList()));
//         }
//         return stats;
//     }

//     private HomepageDTO.WhyChooseDTO convertWhyChooseToDTO(Homepage homepage) {
//         HomepageDTO.WhyChooseDTO whyChoose = new HomepageDTO.WhyChooseDTO();
//         whyChoose.setTitle(homepage.getWhyChooseTitle());
//         if (homepage.getWhyChooseFeatures() != null) {
//             whyChoose.setFeatures(homepage.getWhyChooseFeatures().stream()
//                     .map(WhyChooseFeature::getFeature)
//                     .collect(Collectors.toList()));
//         }
//         return whyChoose;
//     }

//     private HomepageDTO.ArtisanDTO convertArtisanToDTO(Homepage homepage) {
//         HomepageDTO.ArtisanDTO artisan = new HomepageDTO.ArtisanDTO();
//         artisan.setTitle(homepage.getArtisanTitle());
//         artisan.setDescription(homepage.getArtisanDescription());
//         artisan.setLink(homepage.getArtisanLink());
//         artisan.setImage(homepage.getArtisanImage());
//         return artisan;
//     }

//     private HomepageDTO.NewsletterDTO convertNewsletterToDTO(Homepage homepage) {
//         HomepageDTO.NewsletterDTO newsletter = new HomepageDTO.NewsletterDTO();
//         newsletter.setTitle(homepage.getNewsletterTitle());
//         newsletter.setText(homepage.getNewsletterText());
//         return newsletter;
//     }

//     private HomepageDTO.DiningDTO convertDiningToDTO(DiningInfo diningInfo) {
//         HomepageDTO.DiningDTO dining = new HomepageDTO.DiningDTO();
//         dining.setTitle(diningInfo.getTitle());
//         dining.setDescription(diningInfo.getDescription());
//         dining.setButton(diningInfo.getButton());
//         dining.setImage(diningInfo.getImage());
//         return dining;
//     }

//     // Item conversion methods
//     private HomepageDTO.FeaturedProductDTO convertFeaturedProductToDTO(FeaturedProduct product) {
//         HomepageDTO.FeaturedProductDTO dto = new HomepageDTO.FeaturedProductDTO();
//         dto.setId(product.getId());
//         dto.setImage(product.getImage());
//         dto.setCategory(product.getCategory());
//         dto.setName(product.getName());
//         dto.setDescription(product.getDescription());
//         dto.setPrice(product.getPrice());
//         dto.setDisplayOrder(product.getDisplayOrder());
//         return dto;
//     }

//     private HomepageDTO.CategoryItemDTO convertCategoryItemToDTO(CategoryItem item) {
//         HomepageDTO.CategoryItemDTO dto = new HomepageDTO.CategoryItemDTO();
//         dto.setId(item.getId());
//         dto.setImage(item.getImage());
//         dto.setName(item.getName());
//         dto.setCount(item.getCount());
//         dto.setDisplayOrder(item.getDisplayOrder());
//         return dto;
//     }

//     private HomepageDTO.ExperienceCardDTO convertExperienceCardToDTO(ExperienceCard card) {
//         HomepageDTO.ExperienceCardDTO dto = new HomepageDTO.ExperienceCardDTO();
//         dto.setId(card.getId());
//         dto.setTitle(card.getTitle());
//         dto.setDescription(card.getDescription());
//         dto.setLink(card.getLink());
//         dto.setImage(card.getImage());
//         dto.setDisplayOrder(card.getDisplayOrder());
//         return dto;
//     }

//     private HomepageDTO.GalleryProductDTO convertGalleryProductToDTO(GalleryProduct product) {
//         HomepageDTO.GalleryProductDTO dto = new HomepageDTO.GalleryProductDTO();
//         dto.setId(product.getId());
//         dto.setImage(product.getImage());
//         dto.setName(product.getName());
//         dto.setPrice(product.getPrice());
//         dto.setButton(product.getButton());
//         dto.setDisplayOrder(product.getDisplayOrder());
//         return dto;
//     }

//     private HomepageDTO.FeatureItemDTO convertFeatureItemToDTO(FeatureItem item) {
//         HomepageDTO.FeatureItemDTO dto = new HomepageDTO.FeatureItemDTO();
//         dto.setId(item.getId());
//         dto.setTitle(item.getTitle());
//         dto.setDescription(item.getDescription());
//         dto.setLink(item.getLink());
//         dto.setDisplayOrder(item.getDisplayOrder());
//         return dto;
//     }

//     private HomepageDTO.ProductItemDTO convertProductItemToDTO(ProductItem item) {
//         HomepageDTO.ProductItemDTO dto = new HomepageDTO.ProductItemDTO();
//         dto.setId(item.getId());
//         dto.setImage(item.getImage());
//         dto.setBadge(item.getBadge());
//         dto.setCategory(item.getCategory());
//         dto.setName(item.getName());
//         dto.setPrice(item.getPrice());
//         dto.setButtonText(item.getButtonText());
//         dto.setDisplayOrder(item.getDisplayOrder());
//         return dto;
//     }

//     private HomepageDTO.StatisticDTO convertStatisticToDTO(HomepageStatistic statistic) {
//         HomepageDTO.StatisticDTO dto = new HomepageDTO.StatisticDTO();
//         dto.setId(statistic.getId());
//         dto.setNumber(statistic.getNumber());
//         dto.setLabel(statistic.getLabel());
//         dto.setDisplayOrder(statistic.getDisplayOrder());
//         return dto;
//     }

//     // Entity conversion helper methods
//     private void convertHeroToEntity(HomepageDTO.HeroDTO hero, Homepage homepage) {
//         if (hero != null) {
//             homepage.setHeroTitle(hero.getTitle());
//             homepage.setHeroSubtitle(hero.getSubtitle());
//             homepage.setHeroPrimaryCta(hero.getPrimaryCta());
//             homepage.setHeroSecondaryCta(hero.getSecondaryCta());
//             homepage.setHeroBackgroundImage(hero.getBackgroundImage());
//             homepage.setHeroBadge(hero.getBadge());
//             homepage.setHeroButtonText(hero.getButtonText());
//             homepage.setHeroPrimaryButton(hero.getPrimaryButton());
//             homepage.setHeroSecondaryButton(hero.getSecondaryButton());
//             homepage.setHeroImage(hero.getImage());
//         }
//     }

//     private void convertFeaturedToEntity(HomepageDTO.FeaturedDTO featured, Homepage homepage) {
//         if (featured != null) {
//             homepage.setFeaturedTitle(featured.getTitle());
//             homepage.setFeaturedSubtitle(featured.getSubtitle());
//             if (featured.getProducts() != null) {
//                 List<FeaturedProduct> products = featured.getProducts().stream()
//                         .map(dto -> convertFeaturedProductToEntity(dto, homepage))
//                         .collect(Collectors.toList());
//                 homepage.setFeaturedProducts(products);
//             }
//         }
//     }

//     private void convertCategoriesToEntity(HomepageDTO.CategoriesDTO categories, Homepage homepage) {
//         if (categories != null) {
//             homepage.setCategoriesTitle(categories.getTitle());
//             homepage.setCategoriesSubtitle(categories.getSubtitle());
//             if (categories.getItems() != null) {
//                 List<CategoryItem> items = categories.getItems().stream()
//                         .map(dto -> convertCategoryItemToEntity(dto, homepage))
//                         .collect(Collectors.toList());
//                 homepage.setCategoryItems(items);
//             }
//         }
//     }

//     private void convertExperienceToEntity(HomepageDTO.ExperienceDTO experience, Homepage homepage) {
//         if (experience != null) {
//             homepage.setExperienceTitle(experience.getTitle());
//             homepage.setExperienceSubtitle(experience.getSubtitle());
//             if (experience.getCards() != null) {
//                 List<ExperienceCard> cards = experience.getCards().stream()
//                         .map(dto -> convertExperienceCardToEntity(dto, homepage))
//                         .collect(Collectors.toList());
//                 homepage.setExperienceCards(cards);
//             }
//         }
//     }

//     private void convertGalleryToEntity(HomepageDTO.GalleryDTO gallery, Homepage homepage) {
//         if (gallery != null) {
//             homepage.setGalleryTitle(gallery.getTitle());
//             homepage.setGallerySubtitle(gallery.getSubtitle());
//             if (gallery.getProducts() != null) {
//                 List<GalleryProduct> products = gallery.getProducts().stream()
//                         .map(dto -> convertGalleryProductToEntity(dto, homepage))
//                         .collect(Collectors.toList());
//                 homepage.setGalleryProducts(products);
//             }
//             if (gallery.getDining() != null) {
//                 DiningInfo diningInfo = convertDiningToEntity(gallery.getDining(), homepage);
//                 homepage.setDiningInfo(diningInfo);
//             }
//         }
//     }

//     private void convertFeaturesToEntity(HomepageDTO.FeaturesDTO features, Homepage homepage) {
//         if (features != null) {
//             homepage.setFeaturesTitle(features.getTitle());
//             homepage.setFeaturesSubtitle(features.getSubtitle());
//             if (features.getItems() != null) {
//                 List<FeatureItem> items = features.getItems().stream()
//                         .map(dto -> convertFeatureItemToEntity(dto, homepage))
//                         .collect(Collectors.toList());
//                 homepage.setFeatureItems(items);
//             }
//         }
//     }

//     private void convertProductsToEntity(HomepageDTO.ProductsDTO products, Homepage homepage) {
//         if (products != null) {
//             homepage.setProductsTitle(products.getTitle());
//             if (products.getItems() != null) {
//                 List<ProductItem> items = products.getItems().stream()
//                         .map(dto -> convertProductItemToEntity(dto, homepage))
//                         .collect(Collectors.toList());
//                 homepage.setProductItems(items);
//             }
//         }
//     }

//     private void convertStatsToEntity(HomepageDTO.StatsDTO stats, Homepage homepage) {
//         if (stats != null && stats.getItems() != null) {
//             List<HomepageStatistic> statistics = stats.getItems().stream()
//                     .map(dto -> convertStatisticToEntity(dto, homepage))
//                     .collect(Collectors.toList());
//             homepage.setStatistics(statistics);
//         }
//     }

//     private void convertWhyChooseToEntity(HomepageDTO.WhyChooseDTO whyChoose, Homepage homepage) {
//         if (whyChoose != null) {
//             homepage.setWhyChooseTitle(whyChoose.getTitle());
//             if (whyChoose.getFeatures() != null) {
//                 List<WhyChooseFeature> features = whyChoose.getFeatures().stream()
//                         .map(feature -> {
//                             WhyChooseFeature entity = new WhyChooseFeature();
//                             entity.setFeature(feature);
//                             entity.setHomepage(homepage);
//                             return entity;
//                         })
//                         .collect(Collectors.toList());
//                 homepage.setWhyChooseFeatures(features);
//             }
//         }
//     }

//     private void convertArtisanToEntity(HomepageDTO.ArtisanDTO artisan, Homepage homepage) {
//         if (artisan != null) {
//             homepage.setArtisanTitle(artisan.getTitle());
//             homepage.setArtisanDescription(artisan.getDescription());
//             homepage.setArtisanLink(artisan.getLink());
//             homepage.setArtisanImage(artisan.getImage());
//         }
//     }

//     private void convertNewsletterToEntity(HomepageDTO.NewsletterDTO newsletter, Homepage homepage) {
//         if (newsletter != null) {
//             homepage.setNewsletterTitle(newsletter.getTitle());
//             homepage.setNewsletterText(newsletter.getText());
//         }
//     }

//     // Individual item conversion methods
//     private FeaturedProduct convertFeaturedProductToEntity(HomepageDTO.FeaturedProductDTO dto, Homepage homepage) {
//         FeaturedProduct product = new FeaturedProduct();
//         product.setId(dto.getId());
//         product.setImage(dto.getImage());
//         product.setCategory(dto.getCategory());
//         product.setName(dto.getName());
//         product.setDescription(dto.getDescription());
//         product.setPrice(dto.getPrice());
//         product.setDisplayOrder(dto.getDisplayOrder());
//         product.setHomepage(homepage);
//         return product;
//     }

//     private CategoryItem convertCategoryItemToEntity(HomepageDTO.CategoryItemDTO dto, Homepage homepage) {
//         CategoryItem item = new CategoryItem();
//         item.setId(dto.getId());
//         item.setImage(dto.getImage());
//         item.setName(dto.getName());
//         item.setCount(dto.getCount());
//         item.setDisplayOrder(dto.getDisplayOrder());
//         item.setHomepage(homepage);
//         return item;
//     }

//     private ExperienceCard convertExperienceCardToEntity(HomepageDTO.ExperienceCardDTO dto, Homepage homepage) {
//         ExperienceCard card = new ExperienceCard();
//         card.setId(dto.getId());
//         card.setTitle(dto.getTitle());
//         card.setDescription(dto.getDescription());
//         card.setLink(dto.getLink());
//         card.setImage(dto.getImage());
//         card.setDisplayOrder(dto.getDisplayOrder());
//         card.setHomepage(homepage);
//         return card;
//     }

//     private GalleryProduct convertGalleryProductToEntity(HomepageDTO.GalleryProductDTO dto, Homepage homepage) {
//         GalleryProduct product = new GalleryProduct();
//         product.setId(dto.getId());
//         product.setImage(dto.getImage());
//         product.setName(dto.getName());
//         product.setPrice(dto.getPrice());
//         product.setButton(dto.getButton());
//         product.setDisplayOrder(dto.getDisplayOrder());
//         product.setHomepage(homepage);
//         return product;
//     }

//     private FeatureItem convertFeatureItemToEntity(HomepageDTO.FeatureItemDTO dto, Homepage homepage) {
//         FeatureItem item = new FeatureItem();
//         item.setId(dto.getId());
//         item.setTitle(dto.getTitle());
//         item.setDescription(dto.getDescription());
//         item.setLink(dto.getLink());
//         item.setDisplayOrder(dto.getDisplayOrder());
//         item.setHomepage(homepage);
//         return item;
//     }

//     private ProductItem convertProductItemToEntity(HomepageDTO.ProductItemDTO dto, Homepage homepage) {
//         ProductItem item = new ProductItem();
//         item.setId(dto.getId());
//         item.setImage(dto.getImage());
//         item.setBadge(dto.getBadge());
//         item.setCategory(dto.getCategory());
//         item.setName(dto.getName());
//         item.setPrice(dto.getPrice());
//         item.setButtonText(dto.getButtonText());
//         item.setDisplayOrder(dto.getDisplayOrder());
//         item.setHomepage(homepage);
//         return item;
//     }

//     private HomepageStatistic convertStatisticToEntity(HomepageDTO.StatisticDTO dto, Homepage homepage) {
//         HomepageStatistic statistic = new HomepageStatistic();
//         statistic.setId(dto.getId());
//         statistic.setNumber(dto.getNumber());
//         statistic.setLabel(dto.getLabel());
//         statistic.setDisplayOrder(dto.getDisplayOrder());
//         statistic.setHomepage(homepage);
//         return statistic;
//     }

//     private DiningInfo convertDiningToEntity(HomepageDTO.DiningDTO dto, Homepage homepage) {
//         DiningInfo diningInfo = new DiningInfo();
//         diningInfo.setTitle(dto.getTitle());
//         diningInfo.setDescription(dto.getDescription());
//         diningInfo.setButton(dto.getButton());
//         diningInfo.setImage(dto.getImage());
//         diningInfo.setHomepage(homepage);
//         return diningInfo;
//     }
// }