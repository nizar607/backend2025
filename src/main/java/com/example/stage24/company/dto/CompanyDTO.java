package com.example.stage24.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.stage24.homepage.dto.AboutUsDTO;
import com.example.stage24.homepage.service.GeneralHomepageService;
import com.example.stage24.homepage.service.GeneralHomepageService.HomepageResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    
    private Long id;
    
    @NotBlank(message = "Client URL is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Client URL must contain only lowercase letters, numbers, and hyphens")
    private String website;
    
    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String name;
    
    private String logo;
    
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phoneNumber;
    
    private String address;
    
    private String version; // e.g. "v1", "v2", "v3", "custom"
    
    private boolean active;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // For detailed responses
    private List<UserSummaryDTO> users;
    
    private HomepageResponse homepageContent;

    private String homePageVersion;
    
    private AboutUsDTO aboutUs;
    
    // Nested DTOs for summary information
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private List<String> roles;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomepageContentSummaryDTO {
        private Long id;
        private String heroTitle;
        private String heroSubtitle;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AboutUsSummaryDTO {
        private Long id;
        private String storyTitle;
        private String coverImage;
    }
}