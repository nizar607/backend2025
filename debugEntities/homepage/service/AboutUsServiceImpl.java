package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.AboutUsDTO;
import com.example.stage24.homepage.model.*;
import com.example.stage24.homepage.repository.AboutUsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AboutUsServiceImpl implements AboutUsService {
    
    private final AboutUsRepository aboutUsRepository;
    
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
        dto.setCoverImage(aboutUs.getCoverImage());
        
        // Story section
        dto.setStoryTitle(aboutUs.getStoryTitle());
        dto.setStoryContent(aboutUs.getStoryContent());
        
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
        
        // Story section
        aboutUs.setStoryTitle(dto.getStoryTitle());
        aboutUs.setStoryContent(dto.getStoryContent());
        
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
        companyValue.setValueName(dto.getValueName());
        companyValue.setValueDescription(dto.getValueDescription());
        companyValue.setValueIcon(dto.getValueIcon());
        companyValue.setDisplayOrder(dto.getDisplayOrder());
        companyValue.setAboutUs(aboutUs);
        return companyValue;
    }
    
    private AboutUsDTO.TeamMemberDTO convertTeamMemberToDTO(TeamMember teamMember) {
        AboutUsDTO.TeamMemberDTO dto = new AboutUsDTO.TeamMemberDTO();
        dto.setId(teamMember.getId());
        dto.setMemberName(teamMember.getMemberName());
        dto.setMemberPosition(teamMember.getMemberPosition());
        dto.setMemberDescription(teamMember.getMemberDescription());
        dto.setMemberImage(teamMember.getMemberImage());
        dto.setMemberEmail(teamMember.getMemberEmail());
        dto.setMemberLinkedin(teamMember.getMemberLinkedin());
        dto.setDisplayOrder(teamMember.getDisplayOrder());
        return dto;
    }
    
    private TeamMember convertTeamMemberToEntity(AboutUsDTO.TeamMemberDTO dto, AboutUs aboutUs) {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(dto.getId());
        teamMember.setMemberName(dto.getMemberName());
        teamMember.setMemberPosition(dto.getMemberPosition());
        teamMember.setMemberDescription(dto.getMemberDescription());
        teamMember.setMemberImage(dto.getMemberImage());
        teamMember.setMemberEmail(dto.getMemberEmail());
        teamMember.setMemberLinkedin(dto.getMemberLinkedin());
        teamMember.setDisplayOrder(dto.getDisplayOrder());
        teamMember.setAboutUs(aboutUs);
        return teamMember;
    }
    
    private AboutUsDTO.CompanyStatisticDTO convertCompanyStatisticToDTO(CompanyStatistic companyStatistic) {
        AboutUsDTO.CompanyStatisticDTO dto = new AboutUsDTO.CompanyStatisticDTO();
        dto.setId(companyStatistic.getId());
        dto.setStatLabel(companyStatistic.getStatLabel());
        dto.setStatValue(companyStatistic.getStatValue());
        dto.setStatSuffix(companyStatistic.getStatSuffix());
        dto.setStatIcon(companyStatistic.getStatIcon());
        dto.setDisplayOrder(companyStatistic.getDisplayOrder());
        return dto;
    }
    
    private CompanyStatistic convertCompanyStatisticToEntity(AboutUsDTO.CompanyStatisticDTO dto, AboutUs aboutUs) {
        CompanyStatistic companyStatistic = new CompanyStatistic();
        companyStatistic.setId(dto.getId());
        companyStatistic.setStatLabel(dto.getStatLabel());
        companyStatistic.setStatValue(dto.getStatValue());
        companyStatistic.setStatSuffix(dto.getStatSuffix());
        companyStatistic.setStatIcon(dto.getStatIcon());
        companyStatistic.setDisplayOrder(dto.getDisplayOrder());
        companyStatistic.setAboutUs(aboutUs);
        return companyStatistic;
    }
}