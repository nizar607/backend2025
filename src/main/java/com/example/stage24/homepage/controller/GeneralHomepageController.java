package com.example.stage24.homepage.controller;

import com.example.stage24.homepage.service.GeneralHomepageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "General Homepage", description = "API endpoints for retrieving active homepage content by website")
public class GeneralHomepageController {
    
    private final GeneralHomepageService generalHomepageService;
    
    /**
     * Get the current active homepage for a company by website
     * This endpoint returns the homepage version that is currently active (isActive = true)
     * 
     * @param website The website attribute of the company
     * @return The active homepage data with version information
     */
    @Operation(
        summary = "Get active homepage by website",
        description = "Retrieves the currently active homepage (v1, v2, or v3) for a company based on its website URL"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Homepage found and returned successfully"),
        @ApiResponse(responseCode = "404", description = "No active homepage found for the specified website")
    })
    @GetMapping("/by-website/{website}")
    public ResponseEntity<GeneralHomepageService.HomepageResponse> getActiveHomepageByWebsite(
            @Parameter(description = "The website URL of the company", example = "example.com")
            @PathVariable String website) {
        try {
            log.info("Fetching active homepage for website: {}", website);
            
            Optional<Object> homepageOpt = generalHomepageService.getActiveHomepageByWebsite(website);
            
            if (homepageOpt.isPresent()) {
                GeneralHomepageService.HomepageResponse response = 
                    (GeneralHomepageService.HomepageResponse) homepageOpt.get();
                log.info("Found active homepage version {} for website: {}", response.getVersion(), website);
                return ResponseEntity.ok(response);
            } else {
                log.warn("No active homepage found for website: {}", website);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error retrieving active homepage for website {}: {}", website, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Alternative endpoint with query parameter instead of path variable
     * 
     * @param website The website attribute of the company
     * @return The active homepage data with version information
     */
    @Operation(
        summary = "Get active homepage by website (query parameter)",
        description = "Alternative endpoint to retrieve the currently active homepage using a query parameter"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Homepage found and returned successfully"),
        @ApiResponse(responseCode = "404", description = "No active homepage found for the specified website")
    })
    @GetMapping("/active")
    public ResponseEntity<GeneralHomepageService.HomepageResponse> getActiveHomepageByWebsiteParam(
            @Parameter(description = "The website URL of the company", example = "example.com")
            @RequestParam String website) {
        try {
            log.info("Fetching active homepage for website (query param): {}", website);
            
            Optional<Object> homepageOpt = generalHomepageService.getActiveHomepageByWebsite(website);
            
            if (homepageOpt.isPresent()) {
                GeneralHomepageService.HomepageResponse response = 
                    (GeneralHomepageService.HomepageResponse) homepageOpt.get();
                log.info("Found active homepage version {} for website: {}", response.getVersion(), website);
                return ResponseEntity.ok(response);
            } else {
                log.warn("No active homepage found for website: {}", website);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error retrieving active homepage for website {}: {}", website, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}