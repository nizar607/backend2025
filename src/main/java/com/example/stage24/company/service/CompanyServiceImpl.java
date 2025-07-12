package com.example.stage24.company.service;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.homepage.model.AboutUs;
import com.example.stage24.homepage.model.CompanyValue;
import com.example.stage24.homepage.model.TeamMember;
import com.example.stage24.homepage.model.CompanyStatistic;
import com.example.stage24.homepage.repository.AboutUsRepository;
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
    public CompanyDTO createCompany(CompanyDTO companyDTO,User connectedUser) {
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
                    
                    // Check if         name is being changed and if it's available
                    if (!existingCompany.        getName().equals(companyDTO.getName()) &&
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
        //     CompanyDTO.HomepageContentSummaryDTO homepageSummary = new CompanyDTO.HomepageContentSummaryDTO();
        //     homepageSummary.setId(company.getHomepageContent().getId());
        //     homepageSummary.setHeroTitle(company.getHomepageContent().getHeroTitle());
        //     homepageSummary.setHeroSubtitle(company.getHomepageContent().getHeroDescription());
        //     dto.setHomepageContent(homepageSummary);
        // }
        
        // // Add about us summary
        // if (company.getAboutUs() != null) {
        //     CompanyDTO.AboutUsSummaryDTO aboutUsSummary = new CompanyDTO.AboutUsSummaryDTO();
        //     aboutUsSummary.setId(company.getAboutUs().getId());
        //     aboutUsSummary.setStoryTitle(company.getAboutUs().getStoryTitle());
        //     aboutUsSummary.setCoverImage(company.getAboutUs().getCoverImage());
        //     dto.setAboutUs(aboutUsSummary);
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
     * @param companyId the ID of the company
     */
    private void createDefaultAboutUs(Long companyId) {
        AboutUs aboutUs = new AboutUs();
        aboutUs.setCompanyId(companyId);
        
        // Hero Section
        aboutUs.setHeroTitle("Welcome to Our Company");
        aboutUs.setHeroSubtitle("Innovating for a Better Tomorrow");
        aboutUs.setCoverImage("https://example.com/images/cover.jpg");
        
        // Story Section
        aboutUs.setStoryTitle("Our Journey");
        aboutUs.setStoryContent("We started with a mission to create value.");
        aboutUs.setStoryText1("Founded in 2010");
        aboutUs.setStoryText2("Expanded across continents");
        aboutUs.setStoryText3("Serving millions of customers");
        aboutUs.setStoryImage("https://example.com/images/story.jpg");
        
        // Values Section
        aboutUs.setValuesTitle("Our Core Values");
        aboutUs.setValuesDescription("We believe in integrity, innovation, and impact.");
        
        // Team Section
        aboutUs.setTeamTitle("Meet the Team");
        aboutUs.setTeamDescription("Our dedicated professionals.");
        
        // Statistics Section
        aboutUs.setStatsTitle("Our Impact");
        
        // Call to Action Section
        aboutUs.setCtaTitle("Join Us Today");
        aboutUs.setCtaDescription("Become part of our journey.");

        
        // Create default company values
        List<CompanyValue> companyValues = new ArrayList<>();
        
        CompanyValue integrity = new CompanyValue();
        integrity.setTitle("Integrity");
        integrity.setDescription("We do what is right.");
        integrity.setDisplayOrder(1);
        integrity.setAboutUs(aboutUs);
        companyValues.add(integrity);
        
        CompanyValue innovation = new CompanyValue();
        innovation.setTitle("Innovation");
        innovation.setDescription("Always forward-thinking.");
        innovation.setDisplayOrder(2);
        innovation.setAboutUs(aboutUs);
        companyValues.add(innovation);
        
        aboutUs.setCompanyValues(companyValues);
        
        // Create default team members
        List<TeamMember> teamMembers = new ArrayList<>();
        
        TeamMember alice = new TeamMember();
        alice.setName("Alice Johnson");
        alice.setPosition("CEO");
        alice.setBio("Leading the vision.");
        alice.setImage("https://example.com/images/alice.jpg");
        alice.setDisplayOrder(1);
        alice.setAboutUs(aboutUs);
        teamMembers.add(alice);
        
        TeamMember bob = new TeamMember();
        bob.setName("Bob Smith");
        bob.setPosition("CTO");
        bob.setBio("Tech mastermind.");
        bob.setImage("https://example.com/images/bob.jpg");
        bob.setDisplayOrder(2);
        bob.setAboutUs(aboutUs);
        teamMembers.add(bob);
        
        aboutUs.setTeamMembers(teamMembers);
        
        // Create default statistics
        List<CompanyStatistic> companyStatistics = new ArrayList<>();
        
        CompanyStatistic clients = new CompanyStatistic();
        clients.setLabel("Clients");
        clients.setValue("5,000+");
        clients.setDisplayOrder(1);
        clients.setAboutUs(aboutUs);
        companyStatistics.add(clients);
        
        CompanyStatistic countries = new CompanyStatistic();
        countries.setLabel("Countries");
        countries.setValue("25");
        countries.setDisplayOrder(2);
        countries.setAboutUs(aboutUs);
        companyStatistics.add(countries);
        
        aboutUs.setCompanyStatistics(companyStatistics);
        
        // Save the about us with all related entities
        aboutUsRepository.save(aboutUs);
    }
}
                           
       