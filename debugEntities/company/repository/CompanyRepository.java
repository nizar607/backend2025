package com.example.stage24.company.repository;

import com.example.stage24.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    /**
     * Find company by client URL
     */
    Optional<Company> findByClientUrl(String clientUrl);
    
    /**
     * Check if company exists by client URL
     */
    boolean existsByClientUrl(String clientUrl);
    
    /**
     * Find company by name
     */
    Optional<Company> findByName(String name);
    
    /**
     * Check if company exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find all active companies
     */
    @Query("SELECT c FROM Company c WHERE c.active = true")
    java.util.List<Company> findAllActive();
    
    /**
     * Find company with all related data
     */
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.users LEFT JOIN FETCH c.homepageContent LEFT JOIN FETCH c.aboutUs WHERE c.id = :id")
    Optional<Company> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Find company by client URL with all related data
     */
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.users LEFT JOIN FETCH c.homepageContent LEFT JOIN FETCH c.aboutUs WHERE c.clientUrl = :clientUrl")
    Optional<Company> findByClientUrlWithRelations(@Param("clientUrl") String clientUrl);
}