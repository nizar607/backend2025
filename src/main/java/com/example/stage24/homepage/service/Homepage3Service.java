package com.example.stage24.homepage.service;

import com.example.stage24.homepage.dto.Homepage3DTO;
import com.example.stage24.homepage.model.Homepage3;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface Homepage3Service {
    
    Homepage3DTO getHomepage3ByConnectedUser();
    
    List<Homepage3DTO> getAllHomepage3s();
    
    Homepage3DTO getHomepage3ByCompanyId(Long companyId);
    
    Homepage3DTO saveHomepage3(Homepage3DTO homepage3DTO);
    
    boolean existsByCompanyId(Long companyId);
    
    void deleteHomepage3ByCompanyId(Long companyId);
    
    Homepage3DTO updateHomepage3Content(Long id, Homepage3DTO homepage3DTO);
    
    Homepage3DTO updateHomepage3Images(Long id, Map<String, MultipartFile> images);
    
    Homepage3DTO convertToDTO(Homepage3 homepage3);
    
    Homepage3 convertToEntity(Homepage3DTO homepage3DTO);
}