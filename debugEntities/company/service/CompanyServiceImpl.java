package com.example.stage24.company.service;

import com.example.stage24.company.dto.CompanyDTO;
import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {
    
    private final CompanyRepository companyRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
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
    public Optional<CompanyDTO> getCompanyByClientUrl(String clientUrl) {
        return companyRepository.findByClientUrl(clientUrl)
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
    public Optional<CompanyDTO> getCompanyByClientUrlWithRelations(String clientUrl) {
        return companyRepository.findByClientUrlWithRelations(clientUrl)
                .map(this::convertToDetailedDTO);
    }
    
    @Override
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        if (companyRepository.existsByClientUrl(companyDTO.getClientUrl())) {
            throw new IllegalArgumentException("Client URL already exists: " + companyDTO.getClientUrl());
        }
        
        if (companyRepository.existsByName(companyDTO.getName())) {
            throw new IllegalArgumentException("Company name already exists: " + companyDTO.getName());
        }
        
        Company company = convertToEntity(companyDTO);
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        
        Company savedCompany = companyRepository.save(company);
        return convertToDTO(savedCompany);
    }
    
    @Override
    public Optional<CompanyDTO> updateCompany(Long id, CompanyDTO companyDTO) {
        return companyRepository.findById(id)
                .map(existingCompany -> {
                    // Check if client URL is being changed and if it's available
                    if (!existingCompany.getClientUrl().equals(companyDTO.getClientUrl()) &&
                        companyRepository.existsByClientUrl(companyDTO.getClientUrl())) {
                        throw new IllegalArgumentException("Client URL already exists: " + companyDTO.getClientUrl());
                    }
                    
                    // Check if name is being changed and if it's available
                    if (!existingCompany.getName().equals(companyDTO.getName()) &&
                        companyRepository.existsByName(companyDTO.getName())) {
                        throw new IllegalArgumentException("Company name already exists: " + companyDTO.getName());
                    }
                    
                    // Update fields
                    existingCompany.setClientUrl(companyDTO.getClientUrl());
                    existingCompany.setName(companyDTO.getName());
                    existingCompany.setDescription(companyDTO.getDescription());
                    existingCompany.setLogo(companyDTO.getLogo());
                    existingCompany.setPrimaryColor(companyDTO.getPrimaryColor());
                    existingCompany.setSecondaryColor(companyDTO.getSecondaryColor());
                    existingCompany.setContactEmail(companyDTO.getContactEmail());
                    existingCompany.setContactPhone(companyDTO.getContactPhone());
                    existingCompany.setAddress(companyDTO.getAddress());
                    existingCompany.setWebsite(companyDTO.getWebsite());
                    existingCompany.setUpdatedAt(LocalDateTime.now());
                    
                    Company savedCompany = companyRepository.save(existingCompany);
                    return convertToDTO(savedCompany);
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
                    return convertToDTO(savedCompany);
                });
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isClientUrlAvailable(String clientUrl) {
        return !companyRepository.existsByClientUrl(clientUrl);
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
        dto.setClientUrl(company.getClientUrl());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setLogo(company.getLogo());
        dto.setPrimaryColor(company.getPrimaryColor());
        dto.setSecondaryColor(company.getSecondaryColor());
        dto.setContactEmail(company.getContactEmail());
        dto.setContactPhone(company.getContactPhone());
        dto.setAddress(company.getAddress());
        dto.setWebsite(company.getWebsite());
        dto.setActive(company.isActive());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }
    
    @Override
    public Company convertToEntity(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setId(companyDTO.getId());
        company.setClientUrl(companyDTO.getClientUrl());
        company.setName(companyDTO.getName());
        company.setDescription(companyDTO.getDescription());
        company.setLogo(companyDTO.getLogo());
        company.setPrimaryColor(companyDTO.getPrimaryColor());
        company.setSecondaryColor(companyDTO.getSecondaryColor());
        company.setContactEmail(companyDTO.getContactEmail());
        company.setContactPhone(companyDTO.getContactPhone());
        company.setAddress(companyDTO.getAddress());
        company.setWebsite(companyDTO.getWebsite());
        company.setActive(companyDTO.isActive());
        return company;
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
        
        // Add homepage content summary
        if (company.getHomepageContent() != null) {
            CompanyDTO.HomepageContentSummaryDTO homepageSummary = new CompanyDTO.HomepageContentSummaryDTO();
            homepageSummary.setId(company.getHomepageContent().getId());
            homepageSummary.setHeroTitle(company.getHomepageContent().getHeroTitle());
            homepageSummary.setHeroSubtitle(company.getHomepageContent().getHeroDescription());
            dto.setHomepageContent(homepageSummary);
        }
        
        // Add about us summary
        if (company.getAboutUs() != null) {
            CompanyDTO.AboutUsSummaryDTO aboutUsSummary = new CompanyDTO.AboutUsSummaryDTO();
            aboutUsSummary.setId(company.getAboutUs().getId());
            aboutUsSummary.setStoryTitle(company.getAboutUs().getStoryTitle());
            aboutUsSummary.setCoverImage(company.getAboutUs().getCoverImage());
            dto.setAboutUs(aboutUsSummary);
        }
        
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
}