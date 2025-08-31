package com.example.stage24.company.repository;

import com.example.stage24.company.model.Company;
import com.example.stage24.user.domain.User;
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
    Optional<Company> findByWebsite(String website);
    
    /**
     * Check if company exists by client URL
     */
    boolean existsByWebsite(String website);
    
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
     * Find all companies with their users
     */
    @Query("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.users")
    java.util.List<Company> findAllWithUsers();
    
    /**
     * Find company with all related data
     */
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.users LEFT JOIN FETCH c.aboutUs WHERE c.id = :id")
    Optional<Company> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Find company by client URL with all related data
     */
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.users LEFT JOIN FETCH c.aboutUs WHERE c.website = :website")
    Optional<Company> findByWebsiteWithRelations(@Param("website") String website);
    
    /**
     * Find company by user
     */
    Optional<Company> findByUsersContaining(User user);
    
    /**
     * Check if user has a company
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c JOIN c.users u WHERE u.id = :userId")
    boolean hasCompany(@Param("userId") Long userId);

    /**
     * Check if user's company has about us information
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Company c JOIN c.users u JOIN c.aboutUs a WHERE u.id = :userId")
    boolean hasAboutUs(@Param("userId") Long userId);

   
}