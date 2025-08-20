package com.example.stage24.homepage.service;

import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.company.model.Company;
import com.example.stage24.homepage.repository.Homepage1Repository;
import com.example.stage24.homepage.repository.Homepage2Repository;
import com.example.stage24.homepage.repository.Homepage3Repository;
import com.example.stage24.homepage.model.Homepage1;
import com.example.stage24.homepage.model.Homepage2;
import com.example.stage24.homepage.model.Homepage3;
import com.example.stage24.homepage.dto.Homepage1DTO;
import com.example.stage24.homepage.dto.Homepage2DTO;
import com.example.stage24.homepage.dto.Homepage3DTO;
import com.example.stage24.homepage.service.Homepage1Service;
import com.example.stage24.homepage.service.Homepage2Service;
import com.example.stage24.homepage.service.Homepage3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GeneralHomepageService {
    
    private final CompanyRepository companyRepository;
    private final Homepage1Repository homepage1Repository;
    private final Homepage2Repository homepage2Repository;
    private final Homepage3Repository homepage3Repository;
    private final Homepage1Service homepage1Service;
    private final Homepage2Service homepage2Service;
    private final Homepage3Service homepage3Service;
    
    /**
     * Get the active homepage for a company by website
     * Returns the homepage version that is currently active (isActive = true)
     */
    @Transactional(readOnly = true)
    public Optional<Object> getActiveHomepageByWebsite(String website) {
        // First, find the company by website
        Optional<Company> companyOpt = companyRepository.findByWebsite(website);
        
        if (companyOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Company company = companyOpt.get();
        Long companyId = company.getId();
        
        // Check Homepage1 for active status
        Optional<Homepage1> homepage1Opt = homepage1Repository.findByCompanyId(companyId);
        if (homepage1Opt.isPresent() && homepage1Opt.get().getIsActive()) {
            Homepage1DTO homepage1DTO = homepage1Service.convertToDTO(homepage1Opt.get());
            return Optional.of(new HomepageResponse("v1", homepage1DTO));
        }
        
        // Check Homepage2 for active status
        Optional<Homepage2> homepage2Opt = homepage2Repository.findByCompanyId(companyId);
        if (homepage2Opt.isPresent() && homepage2Opt.get().getIsActive()) {
            Homepage2DTO homepage2DTO = homepage2Service.convertToDTO(homepage2Opt.get());
            return Optional.of(new HomepageResponse("v2", homepage2DTO));
        }
        
        // Check Homepage3 for active status
        Optional<Homepage3> homepage3Opt = homepage3Repository.findByCompanyId(companyId);
        if (homepage3Opt.isPresent() && homepage3Opt.get().getIsActive()) {
            Homepage3DTO homepage3DTO = homepage3Service.convertToDTO(homepage3Opt.get());
            return Optional.of(new HomepageResponse("v3", homepage3DTO));
        }
        
        // No active homepage found
        return Optional.empty();
    }
    
    /**
     * Response wrapper that includes the homepage version and data
     */
    public static class HomepageResponse {
        private String version;
        private Object data;
        
        public HomepageResponse(String version, Object data) {
            this.version = version;
            this.data = data;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
    }
}