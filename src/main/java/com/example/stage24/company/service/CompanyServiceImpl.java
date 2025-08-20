package com.example.stage24.company.service;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.homepage.model.AboutUs;
import com.example.stage24.homepage.model.CompanyValue;
import com.example.stage24.homepage.model.TeamMember;
import com.example.stage24.homepage.model.CompanyStatistic;
import com.example.stage24.homepage.repository.AboutUsRepository;
import com.example.stage24.homepage.model.*;
import com.example.stage24.homepage.repository.Homepage1Repository;
import com.example.stage24.homepage.repository.Homepage2Repository;
import com.example.stage24.homepage.repository.Homepage3Repository;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.domain.Role;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional

public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final AboutUsRepository aboutUsRepository;
    private final Homepage1Repository homepage1Repository;
    private final Homepage2Repository homepage2Repository;
    private final Homepage3Repository homepage3Repository;

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAllWithUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllActiveCompanies() {
        return companyRepository.findAllActive().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> getCompanyByWebsite(String website) {
        return companyRepository.findByWebsite(website)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> getCompanyWithRelations(Long id) {
        return companyRepository.findByIdWithRelations(id)
                .map(this::convertToDetailedDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> getCompanyByWebsiteWithRelations(String website) {
        return companyRepository.findByWebsiteWithRelations(website)
                .map(this::convertToDetailedDTO);
    }

    @Override
    public CompanyDTO createCompany(CompanyDTO companyDTO, User connectedUser) {
        if (companyRepository.existsByWebsite(companyDTO.getWebsite())) {
            throw new IllegalArgumentException("Website already exists: " + companyDTO.getWebsite());
        }

        if (companyRepository.existsByName(companyDTO.getName())) {
            throw new IllegalArgumentException("Company name already exists: " + companyDTO.getName());
        }

        Company company = convertToEntity(companyDTO);
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        company.setUsers(List.of(connectedUser));
        connectedUser.setCompany(company);
        Company savedCompany = companyRepository.save(company);

        // Create default about us page
        createDefaultAboutUs(savedCompany.getId());

        // Create default homepage1
        createDefaultHomepage1(savedCompany.getId());
        
        // Create default homepage2
        createDefaultHomepage2(savedCompany.getId());
        
        // Create default homepage3
        createDefaultHomepage3(savedCompany.getId());

        // Reload with users to ensure proper DTO conversion
        Company companyWithUsers = companyRepository.findById(savedCompany.getId()).orElse(savedCompany);
        return convertToDTO(companyWithUsers);
    }

    @Override
    public Optional<CompanyDTO> updateCompany(Long id, CompanyDTO companyDTO) {
        return companyRepository.findById(id)
                .map(existingCompany -> {
                    // Check if website is being changed and if it's available
                    if (!existingCompany.getWebsite().equals(companyDTO.getWebsite()) &&
                            companyRepository.existsByWebsite(companyDTO.getWebsite())) {
                        throw new IllegalArgumentException("Website already exists: " + companyDTO.getWebsite());
                    }

                    // Check if name is being changed and if it's available
                    if (!existingCompany.getName().equals(companyDTO.getName()) &&
                            companyRepository.existsByName(companyDTO.getName())) {
                        throw new IllegalArgumentException("Company name already exists: " + companyDTO.getName());
                    }

                    // Update fields
                    existingCompany.setWebsite(companyDTO.getWebsite());
                    existingCompany.setName(companyDTO.getName());
                    existingCompany.setLogo(companyDTO.getLogo());
                    existingCompany.setEmail(companyDTO.getEmail());
                    existingCompany.setPhoneNumber(companyDTO.getPhoneNumber());
                    existingCompany.setAddress(companyDTO.getAddress());
                    existingCompany.setUpdatedAt(LocalDateTime.now());

                    Company savedCompany = companyRepository.save(existingCompany);
                    // Reload with users to ensure proper DTO conversion
                    Company companyWithUsers = companyRepository.findById(savedCompany.getId()).orElse(savedCompany);
                    return convertToDTO(companyWithUsers);
                });
    }

    @Override
    public boolean deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CompanyDTO> toggleCompanyStatus(Long id) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setActive(!company.isActive());
                    company.setUpdatedAt(LocalDateTime.now());
                    Company savedCompany = companyRepository.save(company);
                    // Reload with users to ensure proper DTO conversion
                    Company companyWithUsers = companyRepository.findById(savedCompany.getId()).orElse(savedCompany);
                    return convertToDTO(companyWithUsers);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isWebsiteAvailable(String website) {
        return !companyRepository.existsByWebsite(website);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCompanyNameAvailable(String name) {
        return !companyRepository.existsByName(name);
    }

    @Override
    public CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setWebsite(company.getWebsite());
        dto.setName(company.getName());
        dto.setLogo(company.getLogo());
        dto.setEmail(company.getEmail());
        dto.setPhoneNumber(company.getPhoneNumber());
        dto.setAddress(company.getAddress());
        dto.setActive(company.isActive());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());

        // Add user summaries
        if (company.getUsers() != null) {
            dto.setUsers(company.getUsers().stream()
                    .map(this::convertUserToSummary)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    @Override
    public Company convertToEntity(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setId(companyDTO.getId());
        company.setWebsite(companyDTO.getWebsite());
        company.setName(companyDTO.getName());
        company.setLogo(companyDTO.getLogo());
        company.setEmail(companyDTO.getEmail());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setAddress(companyDTO.getAddress());
        company.setActive(companyDTO.isActive());
        return company;
    }

    @Override
    public CompanyDTO updateCompanyLogo(Long companyId, String logoUrl) {
        return companyRepository.findById(companyId)
                .map(company -> {
                    company.setLogo(logoUrl);
                    company.setUpdatedAt(LocalDateTime.now());
                    Company savedCompany = companyRepository.save(company);
                    // Reload with users to ensure proper DTO conversion
                    Company companyWithUsers = companyRepository.findById(savedCompany.getId()).orElse(savedCompany);
                    return convertToDTO(companyWithUsers);
                })
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));
    }

    @Override
    public CompanyDTO convertToDetailedDTO(Company company) {
        CompanyDTO dto = convertToDTO(company);

        // Add user summaries
        if (company.getUsers() != null) {
            dto.setUsers(company.getUsers().stream()
                    .map(this::convertUserToSummary)
                    .collect(Collectors.toList()));
        }

        // // Add homepage content summary
        // if (company.getHomepageContent() != null) {
        // CompanyDTO.HomepageContentSummaryDTO homepageSummary = new
        // CompanyDTO.HomepageContentSummaryDTO();
        // homepageSummary.setId(company.getHomepageContent().getId());
        // homepageSummary.setHeroTitle(company.getHomepageContent().getHeroTitle());
        // homepageSummary.setHeroSubtitle(company.getHomepageContent().getHeroDescription());
        // dto.setHomepageContent(homepageSummary);
        // }

        // // Add about us summary
        // if (company.getAboutUs() != null) {
        // CompanyDTO.AboutUsSummaryDTO aboutUsSummary = new
        // CompanyDTO.AboutUsSummaryDTO();
        // aboutUsSummary.setId(company.getAboutUs().getId());
        // aboutUsSummary.setStoryTitle(company.getAboutUs().getStoryTitle());
        // aboutUsSummary.setCoverImage(company.getAboutUs().getCoverImage());
        // dto.setAboutUs(aboutUsSummary);
        // }

        return dto;
    }

    private CompanyDTO.UserSummaryDTO convertUserToSummary(User user) {
        CompanyDTO.UserSummaryDTO summary = new CompanyDTO.UserSummaryDTO();
        summary.setId(user.getId());
        summary.setFirstName(user.getFirstName());
        summary.setLastName(user.getLastName());
        summary.setEmail(user.getEmail());

        if (user.getRoles() != null) {
            summary.setRoles(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList()));
        }

        return summary;
    }

    /**
     * Creates default about us content for a newly created company
     * 
     * @param companyId the ID of the company
     */
    private void createDefaultAboutUs(Long companyId) {
        AboutUs aboutUs = new AboutUs();
        aboutUs.setCompanyId(companyId);

        // Hero Section
        aboutUs.setCoverImage("default-cover-image.png");
        aboutUs.setHeroTitle("About FORMA");
        aboutUs.setHeroSubtitle("Crafting Timeless Elegance Since 1987");

        // Story Section
        aboutUs.setStoryTitle("Our Story");
        aboutUs.setStoryContent(
                "Born from a passion for exceptional design and uncompromising quality, FORMA has been creating furniture that transcends trends for over three decades.");
        aboutUs.setStoryText1(
                "FORMA began in 1987 in a small workshop in Milan, where master craftsman Giovanni Rossi combined traditional Italian woodworking techniques with contemporary design sensibilities. What started as a family business has evolved into a globally recognized brand synonymous with luxury and sophistication.");
        aboutUs.setStoryText2(
                "Our commitment to excellence is evident in every piece we create. We source only the finest materials from sustainable suppliers and work with skilled artisans who share our dedication to perfection. Each piece of furniture is not just a product, but a work of art that tells a story of craftsmanship and care.");
        aboutUs.setStoryText3(
                "Today, FORMA continues to push boundaries while honoring our heritage, creating furniture that seamlessly blends form and function for the modern home.");
        aboutUs.setStoryImage("default-story-image.png");

        // Values Section
        aboutUs.setValuesTitle("Our Values");
        aboutUs.setValuesDescription(
                "The principles that guide everything we do, from design conception to final delivery.");

        // Team Section
        aboutUs.setTeamTitle("Meet Our Team");
        aboutUs.setTeamDescription("The passionate individuals behind every FORMA creation.");

        // Statistics Section
        aboutUs.setStatsTitle("FORMA by the Numbers");

        // Call to Action Section
        aboutUs.setCtaTitle("Experience FORMA");
        aboutUs.setCtaDescription(
                "Visit our showroom to see our craftsmanship up close, or browse our collection online to find the perfect piece for your home. Let us help you create spaces that inspire.");

        // Create default company values
        List<CompanyValue> companyValues = new ArrayList<>();

        CompanyValue qualityFirst = new CompanyValue();
        qualityFirst.setTitle("Quality First");
        qualityFirst.setDescription(
                "We never compromise on quality. Every piece is crafted with attention to detail and built to last generations.");
        qualityFirst.setIcon("bi bi-star");
        qualityFirst.setDisplayOrder(1);
        qualityFirst.setAboutUs(aboutUs);
        companyValues.add(qualityFirst);

        CompanyValue sustainableDesign = new CompanyValue();
        sustainableDesign.setTitle("Sustainable Design");
        sustainableDesign.setDescription(
                "Environmental responsibility guides our choices. We use eco-friendly materials and sustainable practices.");
        sustainableDesign.setIcon("bi bi-tree");
        sustainableDesign.setDisplayOrder(2);
        sustainableDesign.setAboutUs(aboutUs);
        companyValues.add(sustainableDesign);

        CompanyValue customerFocus = new CompanyValue();
        customerFocus.setTitle("Customer Focus");
        customerFocus.setDescription(
                "Your satisfaction is our priority. We listen, adapt, and deliver exactly what you envision for your space.");
        customerFocus.setIcon("bi bi-people");
        customerFocus.setDisplayOrder(3);
        customerFocus.setAboutUs(aboutUs);
        companyValues.add(customerFocus);

        CompanyValue innovation = new CompanyValue();
        innovation.setTitle("Innovation");
        innovation.setDescription(
                "We embrace new technologies and design trends while respecting timeless craftsmanship traditions.");
        innovation.setIcon("bi bi-lightbulb");
        innovation.setDisplayOrder(4);
        innovation.setAboutUs(aboutUs);
        companyValues.add(innovation);

        aboutUs.setCompanyValues(companyValues);

        // Create default team members
        List<TeamMember> teamMembers = new ArrayList<>();

        TeamMember sarah = new TeamMember();
        sarah.setName("Sarah Johnson");
        sarah.setPosition("Founder & CEO");
        sarah.setBio(
                "With over 20 years in furniture design, Sarah leads our vision of creating beautiful, functional spaces.");
        sarah.setImage("default-person-one.png");
        sarah.setDisplayOrder(1);
        sarah.setAboutUs(aboutUs);
        teamMembers.add(sarah);

        TeamMember michael = new TeamMember();
        michael.setName("Michael Chen");
        michael.setPosition("Head of Design");
        michael.setBio(
                "Michael brings innovative design concepts to life, blending modern aesthetics with practical functionality.");
        michael.setImage("default-person-two.png");
        michael.setDisplayOrder(2);
        michael.setAboutUs(aboutUs);
        teamMembers.add(michael);

        TeamMember emily = new TeamMember();
        emily.setName("Emily Rodriguez");
        emily.setPosition("Quality Assurance Director");
        emily.setBio("Emily ensures every piece meets our exacting standards before it reaches your home.");
        emily.setImage("default-person-three.png");
        emily.setDisplayOrder(3);
        emily.setAboutUs(aboutUs);
        teamMembers.add(emily);

        TeamMember david = new TeamMember();
        david.setName("David Thompson");
        david.setPosition("Customer Experience Manager");
        david.setBio("David leads our customer service team, ensuring every interaction exceeds expectations.");
        david.setImage("default-person-four.png");
        david.setDisplayOrder(4);
        david.setAboutUs(aboutUs);
        teamMembers.add(david);

        aboutUs.setTeamMembers(teamMembers);

        // Create default statistics
        List<CompanyStatistic> companyStatistics = new ArrayList<>();

        CompanyStatistic yearsOfExcellence = new CompanyStatistic();
        yearsOfExcellence.setValue("37");
        yearsOfExcellence.setLabel("Years of Excellence");
        yearsOfExcellence.setDisplayOrder(1);
        yearsOfExcellence.setAboutUs(aboutUs);
        companyStatistics.add(yearsOfExcellence);

        CompanyStatistic happyCustomers = new CompanyStatistic();
        happyCustomers.setValue("50K+");
        happyCustomers.setLabel("Happy Customers");
        happyCustomers.setDisplayOrder(2);
        happyCustomers.setAboutUs(aboutUs);
        companyStatistics.add(happyCustomers);

        CompanyStatistic uniqueDesigns = new CompanyStatistic();
        uniqueDesigns.setValue("200+");
        uniqueDesigns.setLabel("Unique Designs");
        uniqueDesigns.setDisplayOrder(3);
        uniqueDesigns.setAboutUs(aboutUs);
        companyStatistics.add(uniqueDesigns);

        CompanyStatistic countriesServed = new CompanyStatistic();
        countriesServed.setValue("15");
        countriesServed.setLabel("Countries Served");
        countriesServed.setDisplayOrder(4);
        countriesServed.setAboutUs(aboutUs);
        companyStatistics.add(countriesServed);

        aboutUs.setCompanyStatistics(companyStatistics);

        // Save the about us with all related entities
        aboutUsRepository.save(aboutUs);
    }

    /**
     * Creates default homepage content for a newly created company
     * 
     * @param companyId the ID of the company
     */


    private void createDefaultHomepage1(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Homepage1 homepage1 = new Homepage1();
        homepage1.setCompany(company);
        homepage1.setIsActive(true);

        // Hero Section
        homepage1.setHeroBadge("New Collection");
        homepage1.setHeroTitle("Timeless Design<br>Modern Living");
        homepage1.setHeroSubtitle(
                "Discover our curated collection of premium furniture and home accessories, crafted for the discerning eye and built to last generations.");
        homepage1.setHeroPrimaryCta("Explore Collection");
        homepage1.setHeroSecondaryCta("Plan Your Room in 3D");
        homepage1.setHeroButtonText("Shop Collection");
        homepage1.setHeroPrimaryButton("Explore Collection");
        homepage1.setHeroSecondaryButton("Plan Your Room in 3D");
        homepage1.setHeroBackgroundImage("homepage-hero-background-image.png");
        homepage1.setHeroImage("homepage-hero-background-image.png");

        // Featured Section
        homepage1.setFeaturedTitle("Featured Collection");
        homepage1.setFeaturedSubtitle("Handpicked pieces that define contemporary elegance");

        // Featured Product 1
        homepage1.setFeaturedProduct1Image("homepage-default-product-one.png");
        homepage1.setFeaturedProduct1Category("Living Room");
        homepage1.setFeaturedProduct1Name("Milano Sectional Sofa");
        homepage1.setFeaturedProduct1Description(
                "A masterpiece of Italian design, featuring premium Italian leather upholstery and solid oak frame. The Milano combines unparalleled comfort with sophisticated aesthetics.");
        homepage1.setFeaturedProduct1Price("$3,299");

        // Featured Product 2
        homepage1.setFeaturedProduct2Image("homepage-default-product-two.png");
        homepage1.setFeaturedProduct2Category("Dining Room");
        homepage1.setFeaturedProduct2Name("Scandinavian Dining Set");
        homepage1.setFeaturedProduct2Description(
                "Clean lines meet warm wood tones in this exceptional dining collection. Crafted from sustainable Nordic oak with precision joinery and minimalist design philosophy.");
        homepage1.setFeaturedProduct2Price("$2,499");

        // Featured Product 3
        homepage1.setFeaturedProduct3Image("homepage-default-product-three.png");
        homepage1.setFeaturedProduct3Category("Bedroom");
        homepage1.setFeaturedProduct3Name("Contemporary Platform Bed");
        homepage1.setFeaturedProduct3Description(
                "Sleek and sophisticated, this platform bed features a floating nightstand design and integrated LED lighting. Perfect for the modern bedroom sanctuary.");
        homepage1.setFeaturedProduct3Price("$1,899");

        // Categories Section
        homepage1.setCategoriesTitle("Shop by Category");
        homepage1.setCategoriesSubtitle("Find the perfect pieces for every room in your home");

        // Category Item 1
        homepage1.setCategoryItem1Image("homepage-default-category-one.png");
        homepage1.setCategoryItem1Name("Living Room");
        homepage1.setCategoryItem1Count("245 Products");

        // Category Item 2
        homepage1.setCategoryItem2Image("homepage-default-category-two.png");
        homepage1.setCategoryItem2Name("Bedroom");
        homepage1.setCategoryItem2Count("180 Products");

        // Category Item 3
        homepage1.setCategoryItem3Image("homepage-default-category-three.png");
        homepage1.setCategoryItem3Name("Dining Room");
        homepage1.setCategoryItem3Count("120 Products");

        // Artisan Section
        homepage1.setArtisanTitle("Artisan Spotlight");
        homepage1.setArtisanDescription(
                "Meet our master craftspeople who bring decades of expertise to every piece. Each item tells a story of passion, precision, and uncompromising quality.");
        homepage1.setArtisanLink("Meet the Team →");
        homepage1.setArtisanImage("homepage-default-artisan.png");

        // Newsletter Section
        homepage1.setNewsletterTitle("Stay Inspired");
        homepage1.setNewsletterText(
                "Subscribe to our newsletter for exclusive previews, design tips, and special offers on our latest collections.");

        // Dining Info Section
        homepage1.setDiningInfoTitle("Dining Experience");
        homepage1.setDiningInfoDescription("Transform every meal into a memorable occasion");
        homepage1.setDiningInfoButton("Explore Collection");
        homepage1.setDiningInfoImage("homepage-dining-info.png");

        // Save the homepage1
        homepage1Repository.save(homepage1);
    }
    
    private void createDefaultHomepage2(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Homepage2 homepage2 = new Homepage2();
        homepage2.setCompany(company);
        homepage2.setIsActive(true);

        // Hero Section
        homepage2.setHeroBadge("New Collection");
        homepage2.setHeroTitle("Timeless Design<br>Modern Living");
        homepage2.setHeroSubtitle(
                "Discover our curated collection of premium furniture and home accessories, crafted for the discerning eye and built to last generations.");
        homepage2.setHeroPrimaryCta("Explore Collection");
        homepage2.setHeroSecondaryCta("Plan Your Room in 3D");
        homepage2.setHeroButtonText("Shop Collection");
        homepage2.setHeroPrimaryButton("Explore Collection");
        homepage2.setHeroSecondaryButton("Plan Your Room in 3D");
        homepage2.setHeroBackgroundImage("homepage-hero-background-image.png");
        homepage2.setHeroImage("homepage-hero-background-image.png");

        // Featured Section
        homepage2.setFeaturedTitle("Featured Collection");
        homepage2.setFeaturedSubtitle("Handpicked pieces that define contemporary elegance");

        // Featured Product 1
        homepage2.setFeaturedProduct1Image("homepage-default-featured-product-one.png");
        homepage2.setFeaturedProduct1Category("Living Room");
        homepage2.setFeaturedProduct1Name("Milano Sectional Sofa");
        homepage2.setFeaturedProduct1Description(
                "A masterpiece of Italian design, featuring premium Italian leather upholstery and solid oak frame. The Milano combines unparalleled comfort with sophisticated aesthetics.");
        homepage2.setFeaturedProduct1Price("$3,299");

        // Featured Product 2
        homepage2.setFeaturedProduct2Image("homepage-default-featured-product-two.png");
        homepage2.setFeaturedProduct2Category("Dining Room");
        homepage2.setFeaturedProduct2Name("Scandinavian Dining Set");
        homepage2.setFeaturedProduct2Description(
                "Clean lines meet warm wood tones in this exceptional dining collection. Crafted from sustainable Nordic oak with precision joinery and minimalist design philosophy.");
        homepage2.setFeaturedProduct2Price("$2,499");

        // Featured Product 3
        homepage2.setFeaturedProduct3Image("homepage-default-featured-product-three.png");
        homepage2.setFeaturedProduct3Category("Bedroom");
        homepage2.setFeaturedProduct3Name("Contemporary Platform Bed");
        homepage2.setFeaturedProduct3Description(
                "Sleek and sophisticated, this platform bed features a floating nightstand design and integrated LED lighting. Perfect for the modern bedroom sanctuary.");
        homepage2.setFeaturedProduct3Price("$1,899");

        // Categories Section
        homepage2.setCategoriesTitle("Shop by Category");
        homepage2.setCategoriesSubtitle("Find the perfect pieces for every room in your home");

        // Category Item 1
        homepage2.setCategoryItem1Image("homepage-default-category-one.png");
        homepage2.setCategoryItem1Name("Living Room");
        homepage2.setCategoryItem1Count("245 Products");

        // Category Item 2
        homepage2.setCategoryItem2Image("homepage-default-category-two.png");
        homepage2.setCategoryItem2Name("Bedroom");
        homepage2.setCategoryItem2Count("180 Products");

        // Category Item 3
        homepage2.setCategoryItem3Image("homepage-default-category-three.png");
        homepage2.setCategoryItem3Name("Dining Room");
        homepage2.setCategoryItem3Count("120 Products");

        // Experience Section
        homepage2.setExperienceTitle("Crafting Excellence");
        homepage2.setExperienceSubtitle("Discover what makes our furniture extraordinary");

        // Gallery Section
        homepage2.setGalleryTitle("Signature Collection");
        homepage2.setGallerySubtitle("Discover pieces that redefine luxury living");

        // Features Section
        homepage2.setFeaturesTitle("Why Choose Nova");
        homepage2.setFeaturesSubtitle("We combine innovative design with exceptional craftsmanship to create furniture that stands the test of time");

        // Feature Items
        homepage2.setFeature1Number("01");
        homepage2.setFeature1Title("Exceptional Craftsmanship");
        homepage2.setFeature1Description("Every piece is handcrafted by master artisans using time-honored techniques and the finest materials sourced globally.");
        homepage2.setFeature1Link("Discover Process →");
        homepage2.setFeature1Image("homepage-default-feature-one.png");

        homepage2.setFeature2Number("02");
        homepage2.setFeature2Title("Bespoke Design");
        homepage2.setFeature2Description("Collaborate with our design team to create furniture that perfectly reflects your vision and lifestyle.");
        homepage2.setFeature2Link("Start Designing →");
        homepage2.setFeature2Image("homepage-default-feature-two.png");

        homepage2.setFeature3Number("03");
        homepage2.setFeature3Title("Sustainable Materials");
        homepage2.setFeature3Description("Committed to environmental responsibility through sustainable sourcing and eco-friendly practices.");
        homepage2.setFeature3Link("Learn More →");
        homepage2.setFeature3Image("homepage-default-feature-three.png");

        homepage2.setFeature4Number("04");
        homepage2.setFeature4Title("Lifetime Warranty");
        homepage2.setFeature4Description("We stand behind our craftsmanship with comprehensive warranty coverage and dedicated support.");
        homepage2.setFeature4Link("View Coverage →");
        homepage2.setFeature4Image("homepage-default-feature-four.png");

        // Products Section
        homepage2.setProductsTitle("Featured Products");

        // Why Choose Section
        homepage2.setWhyChooseTitle("Why Choose Us");

        // Statistics
        homepage2.setStatistic1Number("25+");
        homepage2.setStatistic1Label("Years Experience");

        homepage2.setStatistic2Number("10k+");
        homepage2.setStatistic2Label("Happy Clients");

        homepage2.setStatistic3Number("50+");
        homepage2.setStatistic3Label("Master Artisans");

        // Why Choose Benefits
        homepage2.setBenefit1Title("Free Design Consultation");
        homepage2.setBenefit2Title("White Glove Delivery");
        homepage2.setBenefit3Title("Lifetime Support");

        // Artisan Section
        homepage2.setArtisanTitle("Artisan Spotlight");
        homepage2.setArtisanDescription(
                "Meet our master craftspeople who bring decades of expertise to every piece. Each item tells a story of passion, precision, and uncompromising quality.");
        homepage2.setArtisanLink("Meet the Team →");
        homepage2.setArtisanImage("homepage-default-artisan.png");

        // Newsletter Section
        homepage2.setNewsletterTitle("Stay Inspired");
        homepage2.setNewsletterText(
                "Subscribe to our newsletter for exclusive previews, design tips, and special offers on our latest collections.");

        // Dining Info Section
        homepage2.setDiningInfoTitle("Dining Experience");
        homepage2.setDiningInfoDescription("Transform every meal into a memorable occasion");
        homepage2.setDiningInfoButton("Explore Collection");
        homepage2.setDiningInfoImage("homepage-dining-info.png");

        // Save the homepage2
        homepage2Repository.save(homepage2);
    }

    private void createDefaultHomepage3(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        
        Homepage3 homepage3 = new Homepage3();
        homepage3.setCompany(company);
        homepage3.setIsActive(true);
        homepage3.setCreatedAt(LocalDateTime.now());
        homepage3.setUpdatedAt(LocalDateTime.now());

        // Hero Section
        homepage3.setHeroTitle("Crafted with Passion");
        homepage3.setHeroSubtitle("Discover our handcrafted furniture collection");
        homepage3.setHeroPrimaryCta("Shop Now");
        homepage3.setHeroSecondaryCta("Learn More");
        homepage3.setHeroBackgroundImage("homepage-hero-background-image.png");
        homepage3.setHeroBadge("New Collection");
        homepage3.setHeroButtonText("Explore");
        homepage3.setHeroPrimaryButton("Shop Collection");
        homepage3.setHeroSecondaryButton("View Catalog");
        homepage3.setHeroImage("homepage-default-artisan.png");

        // Featured Section
        homepage3.setFeaturedTitle("Featured Collection");
        homepage3.setFeaturedSubtitle("Handpicked pieces for your home");
        homepage3.setFeaturedProduct1Image("homepage-default-featured-product-one.png");
        homepage3.setFeaturedProduct1Category("Dining");
        homepage3.setFeaturedProduct1Name("Elegant Dining Set");
        homepage3.setFeaturedProduct1Description("Perfect for family gatherings");
        homepage3.setFeaturedProduct1Price("$1,299");
        homepage3.setFeaturedProduct2Image("homepage-default-featured-product-two.png");
        homepage3.setFeaturedProduct2Category("Living Room");
        homepage3.setFeaturedProduct2Name("Luxury Sofa");
        homepage3.setFeaturedProduct2Description("Comfort meets style");
        homepage3.setFeaturedProduct2Price("$2,199");
        homepage3.setFeaturedProduct3Image("homepage-default-featured-product-three.png");
        homepage3.setFeaturedProduct3Category("Living Room");
        homepage3.setFeaturedProduct3Name("Modern Coffee Table");
        homepage3.setFeaturedProduct3Description("Contemporary design");
        homepage3.setFeaturedProduct3Price("$599");

        // Categories Section
        homepage3.setCategoriesTitle("Shop by Category");
        homepage3.setCategoriesSubtitle("Find the perfect piece for every room");
        homepage3.setCategoryItem1Image("homepage-default-category-one.png");
        homepage3.setCategoryItem1Name("Living Room");
        homepage3.setCategoryItem1Count("25+ Items");
        homepage3.setCategoryItem2Image("homepage-default-category-two.png");
        homepage3.setCategoryItem2Name("Bedroom");
        homepage3.setCategoryItem2Count("18+ Items");
        homepage3.setCategoryItem3Image("homepage-default-category-three.png");
        homepage3.setCategoryItem3Name("Dining Room");
        homepage3.setCategoryItem3Count("12+ Items");

        // Experience Section
        homepage3.setExperienceTitle("Exceptional Experience");
        homepage3.setExperienceSubtitle("Quality craftsmanship meets modern design");

        // Gallery Section
        homepage3.setGalleryTitle("Our Gallery");
        homepage3.setGallerySubtitle("Explore our stunning furniture collection");

        // Features Section
        homepage3.setFeaturesTitle("Why Choose Us");
        homepage3.setFeaturesSubtitle("Quality, craftsmanship, and customer satisfaction");

        // Products Section
        homepage3.setProductsTitle("Our Products");

        // Why Choose Section
        homepage3.setWhyChooseTitle("Why Choose Our Furniture");

        // Artisan Section
        homepage3.setArtisanTitle("Master Craftsmen");
        homepage3.setArtisanDescription("Our skilled artisans bring decades of experience to every piece, ensuring exceptional quality and attention to detail in every creation.");
        homepage3.setArtisanLink("/about-us");
        homepage3.setArtisanImage("homepage-default-artisan.png");

        // Newsletter Section
        homepage3.setNewsletterTitle("Stay Connected");
        homepage3.setNewsletterText("Subscribe to our newsletter for the latest collections, design inspiration, and exclusive offers delivered to your inbox.");

        // Dining Info Section
        homepage3.setDiningInfoTitle("Dining in Style");
        homepage3.setDiningInfoDescription("Create unforgettable dining experiences with our premium furniture");
        homepage3.setDiningInfoButton("View Collection");
        homepage3.setDiningInfoImage("homepage-dining-info.png");

        // Save the homepage3
        homepage3Repository.save(homepage3);
    }

    @Override
    public void setHomepageActive(Long companyId, String homepageType) {
        // Get all homepages for the company
        Optional<Homepage1> homepage1 = homepage1Repository.findByCompanyId(companyId);
        Optional<Homepage2> homepage2 = homepage2Repository.findByCompanyId(companyId);
        Optional<Homepage3> homepage3 = homepage3Repository.findByCompanyId(companyId);

        // Set all homepages to inactive
        homepage1.ifPresent(h -> {
            h.setIsActive(false);
            homepage1Repository.save(h);
        });
        
        homepage2.ifPresent(h -> {
            h.setIsActive(false);
            homepage2Repository.save(h);
        });
        
        homepage3.ifPresent(h -> {
            h.setIsActive(false);
            homepage3Repository.save(h);
        });

        // Set the specified homepage to active
        switch (homepageType.toLowerCase()) {
            case "v1":
                homepage1.ifPresent(h -> {
                    h.setIsActive(true);
                    homepage1Repository.save(h);
                });
                break;
            case "v2":
                homepage2.ifPresent(h -> {
                    h.setIsActive(true);
                    homepage2Repository.save(h);
                });
                break;
            case "v3":
                homepage3.ifPresent(h -> {
                    h.setIsActive(true);
                    homepage3Repository.save(h);
                });
                break;
            default:
                throw new IllegalArgumentException("Invalid homepage type: " + homepageType + ". Must be v1, v2, or v3.");
        }
    }
}
                           
       