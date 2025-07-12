package com.example.stage24.homepage.service;

import com.example.stage24.company.model.Company;
import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.homepage.dto.AboutUsDTO;
import com.example.stage24.homepage.model.*;
import com.example.stage24.homepage.repository.AboutUsRepository;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class AboutUsServiceImpl implements AboutUsService {

    private final AboutUsRepository aboutUsRepository;
    private final SharedServiceInterface sharedService;
    private final CompanyRepository companyRepository;

    @Override
    public AboutUsDTO getAboutUsByConnectedUser() {
        User user = sharedService.getConnectedUser().orElseThrow(() -> new RuntimeException("User not found"));
        Company company = companyRepository.findByUsersContaining(user).orElseThrow(() -> new RuntimeException("company by connected user not found"));
        AboutUs aboutUs = aboutUsRepository.findByCompanyId(company.getId()).orElseThrow(() -> new RuntimeException("about us by connected user not found"));
        return convertToDTO(aboutUs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AboutUsDTO> getAllAboutUs() {
        return aboutUsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AboutUsDTO> getAboutUsByCompanyId(Long companyId) {
        return aboutUsRepository.findByCompanyId(companyId)
                .map(this::convertToDTO);
    }

    @Override
    public AboutUsDTO saveAboutUs(AboutUsDTO aboutUsDTO) {
        AboutUs aboutUs = convertToEntity(aboutUsDTO);
        AboutUs savedAboutUs = aboutUsRepository.save(aboutUs);
        return convertToDTO(savedAboutUs);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCompanyId(Long companyId) {
        return aboutUsRepository.existsByCompanyId(companyId);
    }

    @Override
    public void deleteAboutUsByCompanyId(Long companyId) {
        aboutUsRepository.deleteByCompanyId(companyId);
    }

    @Override
    public AboutUsDTO convertToDTO(AboutUs aboutUs) {
        if (aboutUs == null) {
            return null;
        }

        AboutUsDTO dto = new AboutUsDTO();
        dto.setId(aboutUs.getId());
        dto.setCompanyId(aboutUs.getCompanyId());

        // Hero section
        dto.setHeroTitle(aboutUs.getHeroTitle());
        dto.setHeroSubtitle(aboutUs.getHeroSubtitle());
        dto.setCoverImage(aboutUs.getCoverImage());

        // Story section
        dto.setStoryTitle(aboutUs.getStoryTitle());
        dto.setStoryContent(aboutUs.getStoryContent());
        dto.setStoryText1(aboutUs.getStoryText1());
        dto.setStoryText2(aboutUs.getStoryText2());
        dto.setStoryText3(aboutUs.getStoryText3());
        dto.setStoryImage(aboutUs.getStoryImage());

        // Values section
        dto.setValuesTitle(aboutUs.getValuesTitle());
        dto.setValuesDescription(aboutUs.getValuesDescription());
        if (aboutUs.getCompanyValues() != null) {
            dto.setCompanyValues(aboutUs.getCompanyValues().stream()
                    .map(this::convertCompanyValueToDTO)
                    .collect(Collectors.toList()));
        }

        // Team section
        dto.setTeamTitle(aboutUs.getTeamTitle());
        dto.setTeamDescription(aboutUs.getTeamDescription());
        if (aboutUs.getTeamMembers() != null) {
            dto.setTeamMembers(aboutUs.getTeamMembers().stream()
                    .map(this::convertTeamMemberToDTO)
                    .collect(Collectors.toList()));
        }

        // Statistics section
        dto.setStatsTitle(aboutUs.getStatsTitle());
        if (aboutUs.getCompanyStatistics() != null) {
            dto.setCompanyStatistics(aboutUs.getCompanyStatistics().stream()
                    .map(this::convertCompanyStatisticToDTO)
                    .collect(Collectors.toList()));
        }

        // Call to Action section
        dto.setCtaTitle(aboutUs.getCtaTitle());
        dto.setCtaDescription(aboutUs.getCtaDescription());

        return dto;
    }

    @Override
    public AboutUs convertToEntity(AboutUsDTO dto) {
        if (dto == null) {
            return null;
        }

        AboutUs aboutUs = new AboutUs();
        aboutUs.setId(dto.getId());
        aboutUs.setCompanyId(dto.getCompanyId());
        aboutUs.setCoverImage(dto.getCoverImage());

        // Hero section
        aboutUs.setHeroTitle(dto.getHeroTitle());
        aboutUs.setHeroSubtitle(dto.getHeroSubtitle());
        aboutUs.setCoverImage(dto.getCoverImage());

        // Story section
        aboutUs.setStoryTitle(dto.getStoryTitle());
        aboutUs.setStoryContent(dto.getStoryContent());
        aboutUs.setStoryText1(dto.getStoryText1());
        aboutUs.setStoryText2(dto.getStoryText2());
        aboutUs.setStoryText3(dto.getStoryText3());
        aboutUs.setStoryImage(dto.getStoryImage());

        // Values section
        aboutUs.setValuesTitle(dto.getValuesTitle());
        aboutUs.setValuesDescription(dto.getValuesDescription());
        if (dto.getCompanyValues() != null) {
            List<CompanyValue> companyValues = dto.getCompanyValues().stream()
                    .map(valueDTO -> convertCompanyValueToEntity(valueDTO, aboutUs))
                    .collect(Collectors.toList());
            aboutUs.setCompanyValues(companyValues);
        }

        // Team section
        aboutUs.setTeamTitle(dto.getTeamTitle());
        aboutUs.setTeamDescription(dto.getTeamDescription());
        if (dto.getTeamMembers() != null) {
            List<TeamMember> teamMembers = dto.getTeamMembers().stream()
                    .map(memberDTO -> convertTeamMemberToEntity(memberDTO, aboutUs))
                    .collect(Collectors.toList());
            aboutUs.setTeamMembers(teamMembers);
        }

        // Statistics section
        aboutUs.setStatsTitle(dto.getStatsTitle());
        if (dto.getCompanyStatistics() != null) {
            List<CompanyStatistic> companyStatistics = dto.getCompanyStatistics().stream()
                    .map(statDTO -> convertCompanyStatisticToEntity(statDTO, aboutUs))
                    .collect(Collectors.toList());
            aboutUs.setCompanyStatistics(companyStatistics);
        }

        // Call to Action section
        aboutUs.setCtaTitle(dto.getCtaTitle());
        aboutUs.setCtaDescription(dto.getCtaDescription());

        return aboutUs;
    }

    private AboutUsDTO.CompanyValueDTO convertCompanyValueToDTO(CompanyValue companyValue) {
        AboutUsDTO.CompanyValueDTO dto = new AboutUsDTO.CompanyValueDTO();
        dto.setId(companyValue.getId());
        dto.setTitle(companyValue.getTitle());
        dto.setDescription(companyValue.getDescription());
        dto.setDisplayOrder(companyValue.getDisplayOrder());
        return dto;
    }

    private CompanyValue convertCompanyValueToEntity(AboutUsDTO.CompanyValueDTO dto, AboutUs aboutUs) {
        CompanyValue companyValue = new CompanyValue();
        companyValue.setId(dto.getId());
        companyValue.setTitle(dto.getTitle());
        companyValue.setDescription(dto.getDescription());
        companyValue.setDisplayOrder(dto.getDisplayOrder());
        companyValue.setAboutUs(aboutUs);
        return companyValue;
    }

    private AboutUsDTO.TeamMemberDTO convertTeamMemberToDTO(TeamMember teamMember) {
        AboutUsDTO.TeamMemberDTO dto = new AboutUsDTO.TeamMemberDTO();
        dto.setId(teamMember.getId());
        dto.setName(teamMember.getName());
        dto.setPosition(teamMember.getPosition());
        dto.setBio(teamMember.getBio());
        dto.setImage(teamMember.getImage());
        dto.setDisplayOrder(teamMember.getDisplayOrder());
        return dto;
    }

    private TeamMember convertTeamMemberToEntity(AboutUsDTO.TeamMemberDTO dto, AboutUs aboutUs) {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(dto.getId());
        teamMember.setName(dto.getName());
        teamMember.setPosition(dto.getPosition());
        teamMember.setBio(dto.getBio());
        teamMember.setImage(dto.getImage());
        teamMember.setDisplayOrder(dto.getDisplayOrder());
        teamMember.setAboutUs(aboutUs);
        return teamMember;
    }

    private AboutUsDTO.CompanyStatisticDTO convertCompanyStatisticToDTO(CompanyStatistic companyStatistic) {
        AboutUsDTO.CompanyStatisticDTO dto = new AboutUsDTO.CompanyStatisticDTO();
        dto.setId(companyStatistic.getId());
        dto.setLabel(companyStatistic.getLabel());
        dto.setValue(companyStatistic.getValue());
        dto.setDisplayOrder(companyStatistic.getDisplayOrder());
        return dto;
    }

    private CompanyStatistic convertCompanyStatisticToEntity(AboutUsDTO.CompanyStatisticDTO dto, AboutUs aboutUs) {
        CompanyStatistic companyStatistic = new CompanyStatistic();
        companyStatistic.setId(dto.getId());
        companyStatistic.setLabel(dto.getLabel());
        companyStatistic.setValue(dto.getValue());
        companyStatistic.setDisplayOrder(dto.getDisplayOrder());
        companyStatistic.setAboutUs(aboutUs);
        return companyStatistic;
    }

    /**
     * Create or update AboutUs content based on JWT token authentication
     * Uses the token to get the connected user and verify which company they belong
     * to
     * 
     * @param aboutUsDTO the about us data to save or update
     * @param token      the JWT token to extract user information
     * @return saved or updated AboutUsDTO
     * @throws RuntimeException if user not found or user has no company
     */
    public AboutUsDTO createOrUpdateAboutUs(AboutUsDTO aboutUsDTO) {
        // Extract username from JWT token

        // Find user by email (username is email in this system)
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user belongs to a company
        if (user.getCompany() == null) {
            throw new RuntimeException("User is not associated with any company");
        }

        Long companyId = user.getCompany().getId();

        // Check if AboutUs already exists for this company
        Optional<AboutUs> existingAboutUs = aboutUsRepository.findByCompanyId(companyId);

        if (existingAboutUs.isPresent()) {
            // Update existing AboutUs
            AboutUs aboutUsToUpdate = existingAboutUs.get();
            aboutUsDTO.setId(aboutUsToUpdate.getId());
            aboutUsDTO.setCompanyId(companyId);

            AboutUs updatedAboutUs = convertToEntity(aboutUsDTO);
            AboutUs savedAboutUs = aboutUsRepository.save(updatedAboutUs);
            return convertToDTO(savedAboutUs);
        } else {
            // Create new AboutUs
            aboutUsDTO.setId(null); // Ensure it's a new entity
            aboutUsDTO.setCompanyId(companyId);

            AboutUs newAboutUs = convertToEntity(aboutUsDTO);
            AboutUs savedAboutUs = aboutUsRepository.save(newAboutUs);
            return convertToDTO(savedAboutUs);
        }
    }

}