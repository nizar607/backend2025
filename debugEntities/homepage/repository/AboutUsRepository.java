package com.example.stage24.homepage.repository;

import com.example.stage24.homepage.model.AboutUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AboutUsRepository extends JpaRepository<AboutUs, Long> {
    
    /**
     * Find about us content by company ID
     */
    Optional<AboutUs> findByCompanyId(Long companyId);
    
    /**
     * Check if about us content exists for a company
     */
    boolean existsByCompanyId(Long companyId);
    
    /**
     * Delete about us content by company ID
     */
    void deleteByCompanyId(Long companyId);
    
    /**
     * Find about us with all related entities
     */
    // @Query("SELECT a FROM AboutUs a " +
    //        "LEFT JOIN FETCH a.companyValues cv " +
    //        "LEFT JOIN FETCH a.teamMembers tm " +
    //        "LEFT JOIN FETCH a.companyStatistics cs " +
    //        "WHERE a.companyId = :companyId " +
    //        "ORDER BY cv.displayOrder ASC, tm.displayOrder ASC, cs.displayOrder ASC")
    // Optional<AboutUs> findByCompanyIdWithAllRelations(@Param("companyId") Long companyId);
}